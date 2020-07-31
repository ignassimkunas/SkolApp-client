package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
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
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity {
    final String url ="http://94.237.45.148:1176/";
    TextView textView;
    RequestQueue queue;
    ArrayList<Double> igSumaArr;
    ArrayList<Double> gabSumaArr;
    DecimalFormat format = new DecimalFormat("0.00");

    public interface VolleyCallBack{
        void onSuccess();
    }
    //paspaudus ant pačios skolos iššoka promptas "Ar skola sumokėta?"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addPayment = findViewById(R.id.addPayment);
        textView = findViewById(R.id.textView);
        queue = Volley.newRequestQueue(this);
        updateValue();
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPayment(30.4, "sample payment", new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        updateValue();
                    }
                });
            }
        });
    }
    public void updateValue(){
        igSumaArr = new ArrayList<>();
        gabSumaArr = new ArrayList<>();
        getSkol(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                setSkolValue();
            }
        });
    }
    public void setSkolValue(){
        Log.i("sumarr", Integer.toString(gabSumaArr.size()));
        double igSum = 0;
        double gabSum = 0;
        for (Double i : igSumaArr){
            igSum += i;
            Log.i("paymentsIg", i.toString());
        }
        igSum = igSum / 2;
        for (Double i : gabSumaArr){
            gabSum += i;
            Log.i("paymentsGab", i.toString());
        }
        Log.i("igValue", Double.toString(igSum));
        gabSum = gabSum / 2;
        Log.i("gabValue", Double.toString(gabSum));
        double kof = igSum - gabSum;
        textView.setText(format.format(kof));
        if (kof > 0){
            textView.setTextColor(Color.GREEN);
        }
        else if (kof < 0){
            textView.setTextColor(Color.RED);
        }
    }

    public void sendPayment(double ammount, String description, final VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "add_payment/ignas&" +ammount +'&' + description  ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Added payment of 30.4 euros", Toast.LENGTH_SHORT).show();
                        volleyCallBack.onSuccess();
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Payment adding unsuccessful", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }
    //istraukia istorija ir suupdatina kad rodytu skola.
    //** not finished **
    public void getSkol(final VolleyCallBack volleyCallBack){
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
                            Log.i("JSONRESPONSE", jsonArray.toString());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        volleyCallBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestIg);
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
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        volleyCallBack.onSuccess();
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestGab);
    }
}