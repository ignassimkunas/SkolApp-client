package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    final String url ="http://94.237.45.148:1176/";
    TextView skolaView;
    TextView thisMonthSpending;
    TextView currentMonth;
    RequestQueue queue;
    ArrayList<Double> igSumaArr;
    ArrayList<Double> gabSumaArr;
    DecimalFormat format = new DecimalFormat("0.00");

    //TODO: parodyt kuris menuo(Spendings in August)
    //TODO: padaryt kad rodytu ataskaita pirkiniu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toPayment = findViewById(R.id.toPayment);
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        skolaView = findViewById(R.id.textView);
        thisMonthSpending = findViewById(R.id.thisMonthSpending);
        currentMonth = findViewById(R.id.monthTitle);
        queue = Volley.newRequestQueue(this);
        updateValue(new VolleyCallBackNoValue() {
            @Override
            public void onSuccess() {
                if (Float.parseFloat(skolaView.getText().toString()) < 0){
                    skolaView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            OnSkolRemoveDialogue dialogue = new OnSkolRemoveDialogue();
                            dialogue.show(getSupportFragmentManager(), "RemoveSkol");
                            return true;
                        }
                    });
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateValue(new VolleyCallBackNoValue() {
                    @Override
                    public void onSuccess() {}
                });
                refreshLayout.setRefreshing(false);
            }
        });
        toPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });
    }
    public void updateValue(final VolleyCallBackNoValue volleyCallBack){
        getSkolIg(new VolleyCallBack() {
            @Override
            public void onSuccess(float value) {
                final float igSum = value;
                getSkolGab(new VolleyCallBack() {
                    @Override
                    public void onSuccess(float value) {
                        double kof = igSum - value;
                        kof = Math.round(kof * 100.0) / 100.0;
                        skolaView.setText(format.format(kof));
                        volleyCallBack.onSuccess();
                    }
                });
            }
        });
        getTotalSpendings(new VolleyCallBack() {
            @Override
            public void onSuccess(float value) {
                DateFormat dateFormat = new SimpleDateFormat("MMMM");
                currentMonth.setText("Spendings in " + dateFormat.format(Calendar.getInstance().getTime()) + ":");
                thisMonthSpending.setText(Float.toString(value));
            }
        });
    }
    public void getSkolIg(final VolleyCallBack volleyCallBack){
        igSumaArr = new ArrayList<>();
        StringRequest stringRequestIg = new StringRequest(Request.Method.GET, url + "ignas",
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
    public void getSkolGab(final VolleyCallBack volleyCallBack){
        gabSumaArr = new ArrayList<>();
        StringRequest stringRequestGab = new StringRequest(Request.Method.GET, url + "gabija",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++){
                                gabSumaArr.add(jsonArray.getJSONObject(i).getDouble("ammount"));
                            }
                            Log.i("JSONRESPONSE", jsonArray.toString());
                            float gabSum = 0;
                            for (Double i : gabSumaArr){
                                gabSum += i;
                            }
                            gabSum = gabSum / 2;
                            volleyCallBack.onSuccess(gabSum);
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
        queue.add(stringRequestGab);
    }
    public void getTotalSpendings(final VolleyCallBack volleyCallBack){
        final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final DateFormat dateFormatNoDay = new SimpleDateFormat("yyyy-MM");
        StringRequest stringRequestTotal = new StringRequest(Request.Method.GET, url + "monthly_spendings/" + dateFormatNoDay.format(Calendar.getInstance().getTime()) + "-01&" + simpleDateFormat.format(Calendar.getInstance().getTime()),
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