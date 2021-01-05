package com.example.skolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void getUsers(final VolleyCallBackList volleyCallBackList){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://5.20.217.145:1176/users";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            volleyCallBackList.onSuccess(array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        final ListView listView = view.findViewById(R.id.listView);
        final ArrayList<String> users = new ArrayList<>();
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        getUsers(new VolleyCallBackList() {
            @Override
            public void onSuccess(JSONArray list) throws JSONException {
                final String currentUser = sharedPreferences.getString("currentUser", "Ignas");
                for (int i = 0; i < list.length(); i++){
                    if (!list.getJSONObject(i).getString("user").equals(currentUser)) users.add(list.getJSONObject(i).getString("user"));
                }
                //Register user
                //Add second pie chart
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.white_list, users){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView textView = view.findViewById(android.R.id.text1);
                        String text = (String) textView.getText();

                        String selectedUser = sharedPreferences.getString("selectedUser", "");

                        if (text.equals(selectedUser)){
                            textView.setTextColor(Color.GREEN);
                        }

                        return view;
                    }
                };

                listView.setAdapter(adapter);
                listView.setDivider(null);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(android.R.id.text1);
                String user = (String) textView.getText();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedUser", user);
                editor.apply();
                for (int i = 0; i < listView.getChildCount(); i++){
                    TextView child = listView.getChildAt(i).findViewById(android.R.id.text1);
                    if (child.getCurrentTextColor() != Color.WHITE){
                        child.setTextColor(Color.WHITE);
                    }
                }
                textView.setTextColor(Color.GREEN);

            }
        });
        return view;
    }
}