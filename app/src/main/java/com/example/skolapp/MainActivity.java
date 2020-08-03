package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import java.text.DecimalFormat;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity {
    final String url ="http://94.237.45.148:1176/";
    public TextView skolaView;
    RequestQueue queue;
    ArrayList<Double> igSumaArr;
    ArrayList<Double> gabSumaArr;
    DecimalFormat format = new DecimalFormat("0.00");

    //TODO: padaryt kiek si men isleista
    //TODO: padaryt kad rodytu ataskaita pirkiniu
    //TODO: padaryt kad jei ne skola tai neleidzia skola grazint
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toPayment = findViewById(R.id.toPayment);
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        skolaView = findViewById(R.id.textView);
        queue = Volley.newRequestQueue(this);
        updateValue(new VolleyCallBackNoValue() {
            @Override
            public void onSuccess() {
                skolaView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnSkolRemoveDialogue dialogue = new OnSkolRemoveDialogue();
                        dialogue.show(getSupportFragmentManager(), "RemoveSkol");
                    }
                });
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateValue(new VolleyCallBackNoValue() {
                    @Override
                    public void onSuccess() {

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
                        if (kof >= 0){
                            //green
                            skolaView.setTextColor(Color.parseColor("#9AEF83"));
                        }
                        else if (kof < 0){
                            //raudona
                            skolaView.setTextColor(Color.parseColor("#FD8787"));
                        }
                        skolaView.setText(format.format(kof));
                        volleyCallBack.onSuccess();
                    }
                });
            }
        });
    }

    //istraukia istorija ir suupdatina kad rodytu skola.
    //** not finished **
    public void getSkolIg(final VolleyCallBack volleyCallBack){
        igSumaArr = new ArrayList<>();
        // Request a string response from the provided URL.
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
}