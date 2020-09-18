package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
    String url;
    TextView skolaView;
    TextView thisMonthSpending;
    TextView currentMonth;
    RequestQueue queue;
    double currentValue;
    ArrayList<Double> igSumaArr;
    ArrayList<Double> gabSumaArr;
    DecimalFormat format = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toPayment = findViewById(R.id.toPayment);
        Button toSummary = findViewById(R.id.toSummary);
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        skolaView = findViewById(R.id.textView);
        thisMonthSpending = findViewById(R.id.thisMonthSpending);
        currentMonth = findViewById(R.id.monthTitle);
        queue = Volley.newRequestQueue(this);
        updateValue(new VolleyCallBack() {

            @Override
            public void onSuccess(final double value) {
                currentValue = value;
            }
        });
        skolaView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, Double.toString(currentValue), Toast.LENGTH_SHORT).show();
                if (currentValue < 0){
                    OnSkolRemoveDialogue dialogue = new OnSkolRemoveDialogue();
                    dialogue.show(getSupportFragmentManager(), "RemoveSkol");
                }
                return true;
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });
        toSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                startActivity(intent);
            }
        });
    }
    public void updateValue(final VolleyCallBack volleyCallBack){
        if (getCurrentSsid(this).equals("\"GabAndIg5Ghz\"") || getCurrentSsid(this).equals("\"GabAndIg24Ghz\"") || getCurrentSsid(this).equals("\"GabAndIg\"")){
            url = "http://192.168.0.45:1176/";
        }
        else{
            url = "http://5.20.217.145:1176/";
        }
        getSkolIg(new VolleyCallBack() {
            @Override
            public void onSuccess(double value) {
                final double igSum = value;
                getSkolGab(new VolleyCallBack() {
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
    public static String getCurrentSsid(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        }
        return null;
    }
    public void getSkolIg(final VolleyCallBack volleyCallBack){
        igSumaArr = new ArrayList<>();
        StringRequest stringRequestIg = new StringRequest(Request.Method.GET, url + "Ignas",
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
        StringRequest stringRequestGab = new StringRequest(Request.Method.GET, url + "Gabija",
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