package com.example.skolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String url = "http://5.20.217.145:1176/by_month/";
    String currentUser;
    RequestQueue queue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatFragment newInstance(String param1, String param2) {
        StatFragment fragment = new StatFragment();
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

    public void createPieChart(PieChart chart, List<PieEntry> entries, String title){
        PieDataSet set = new PieDataSet(entries, title);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "solway_medium.ttf");
        int[] pink = {255, 223, 223};
        int[] green = {193, 255, 181};
        int[] orange = {252, 218, 108};
        set.setColors(new int[] {Color.rgb(pink[0], pink[1], pink[2]), Color.rgb(green[0], green[1], green[2]), Color.rgb(orange[0], orange[1], orange[2])});
        PieData data = new PieData(set);
        chart.setData(data);
        chart.invalidate();

        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setCenterText(title);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleColor(Color.TRANSPARENT);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);
        chart.setCenterTextTypeface(face);
        chart.setEntryLabelTypeface(face);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setCenterTextColor(Color.WHITE);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    public void getMonthData(final VolleyCallBack volleyCallBack, int monthNumber){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + monthNumber +"&" + currentUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            double sum = 0;
                            for (int i = 0; i < array.length(); i++){
                                sum += array.getJSONObject(i).getDouble("ammount");
                            }
                            volleyCallBack.onSuccess(sum);
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
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("currentUser", "Ignas");
        final PieChart firstChart = view.findViewById(R.id.first_pie_chart);
        queue = Volley.newRequestQueue(getActivity());

        final List<PieEntry> firstEntries = new ArrayList<>();

        getMonthData(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                if (value > 0) firstEntries.add(new PieEntry((float) value, "November"));
            }
        }, 11);

        getMonthData(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                if (value > 0) firstEntries.add(new PieEntry((float) value, "December"));
            }
        }, 12);

        getMonthData(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                if (value > 0) firstEntries.add(new PieEntry((float) value, "January"));
                createPieChart(firstChart, firstEntries, "Monthly Spendings for " + currentUser);
            }
        }, 1);

        return view;
    }
}