package com.example.skolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebtFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class DebtFragment extends Fragment {

    String url = "http://192.168.0.16:1176/";



    TextView skolaView;
    TextView thisMonthSpending;
    TextView currentMonth;
    RequestQueue queue;
    double currentValue;
    ArrayList<Double> igSumaArr;
    ArrayList<Double> userSumArr;
    DecimalFormat format = new DecimalFormat("0.00");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DebtFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DebtFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebtFragment newInstance(String param1, String param2) {
        DebtFragment fragment = new DebtFragment();
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

    String currentUser;
    String selectedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debt, container, false);
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("currentUser", "Ignas");
        selectedUser = sharedPreferences.getString("selectedUser", "Kate");
        Button toPayment = view.findViewById(R.id.toPayment);
        Button toSummary = view.findViewById(R.id.toSummary);
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        skolaView = view.findViewById(R.id.textView);
        thisMonthSpending = view.findViewById(R.id.thisMonthSpending);
        currentMonth = view.findViewById(R.id.monthTitle);
        queue = Volley.newRequestQueue(getContext());
        updateValue(new VolleyCallBack() {

            @Override
            public void onSuccess(final double value) {
                currentValue = value;
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Refresh", "Refreshing");
                updateValue(new VolleyCallBack() {
                    @Override
                    public void onSuccess(double value) {
                        currentValue = value;
                    }
                });
                refreshLayout.setRefreshing(false);
            }
        });
        toPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });
        toSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SummaryActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }



    public void updateValue(final VolleyCallBack volleyCallBack){
        getSkolIg(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                final double igSum = value;
                getSkolUser(new VolleyCallBack() {
                    @Override
                    public void onSuccess(double value) {
                        double kof = igSum - value;
                        kof = Math.round(kof * 100.0) / 100.0;
                        skolaView.setText(format.format(kof));
                        volleyCallBack.onSuccess(kof);
                    }
                });
            }
        });
        getTotalSpendings(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                DateFormat dateFormat = new SimpleDateFormat("MMMM");
                currentMonth.setText("Spendings in " + dateFormat.format(Calendar.getInstance().getTime()) + ":");
                thisMonthSpending.setText(Double.toString(Math.round(value*100.0)/100.0));
            }
        });
    }

    public void getSkolIg(final VolleyCallBack volleyCallBack){
        igSumaArr = new ArrayList<>();
        StringRequest stringRequestIg = new StringRequest(Request.Method.GET, url +"payments/" + currentUser + "&" + selectedUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++){
                                igSumaArr.add(jsonArray.getJSONObject(i).getDouble("ammount"));
                            }
                            float igSum = 0;
                            for (Double i : igSumaArr){
                                igSum += i;
                            }
                            igSum = igSum / 2;
                            volleyCallBack.onSuccess(igSum);
                            Log.i("JSONRESPONSE", jsonArray.toString());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestIg);
    }
    public void getSkolUser(final VolleyCallBack volleyCallBack){
        userSumArr = new ArrayList<>();
        StringRequest stringRequestUser = new StringRequest(Request.Method.GET, url +"payments/" + selectedUser + "&" + currentUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++){
                                userSumArr.add(jsonArray.getJSONObject(i).getDouble("ammount"));
                            }
                            Log.i("JSONRESPONSE", jsonArray.toString());
                            float userSum = 0;
                            for (Double i : userSumArr){
                                userSum += i;
                            }
                            userSum = userSum / 2;
                            volleyCallBack.onSuccess(userSum);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestUser);
    }
    public void getTotalSpendings(final VolleyCallBack volleyCallBack){
        final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final DateFormat dateFormatNoDay = new SimpleDateFormat("yyyy-MM");
        StringRequest stringRequestTotal = new StringRequest(Request.Method.GET, url + "monthly_spendings/" + dateFormatNoDay.format(new Date()) + "-01&" + simpleDateFormat.format(new Date().getTime()) + "&" + currentUser + "&" + selectedUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Double> sumArray = new ArrayList<>();
                        float sum = 0;
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++){
                                sumArray.add(jsonArray.getJSONObject(i).getDouble("ammount"));
                            }
                            for (Double i : sumArray){
                                sum += i;
                            }
                            volleyCallBack.onSuccess(sum);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                        Log.i("Monthly", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestTotal);
    }
}