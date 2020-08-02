package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.text.DecimalFormat;
import java.util.ArrayList;


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
        Button toPayment = findViewById(R.id.toPayment);
        textView = findViewById(R.id.textView);
        queue = Volley.newRequestQueue(this);
        updateValue();
        toPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });
    }
    public void updateValue(){
        getSkol(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                setSkolValue();
            }
        });
    }
    //padaryt su callbacku lol
    public void setSkolValue(){

        Log.i("sumarr", Integer.toString(gabSumaArr.size()));
        float igSum = 0;
        float gabSum = 0;
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
        float kof = igSum - gabSum;
        if (kof > 0){
            textView.setText(format.format(igSum - gabSum));
            textView.setTextColor(Color.GREEN);
        }
        else if (kof < 0){
            textView.setText(format.format(igSum - gabSum));
            textView.setTextColor(Color.RED);
        }
    }


    //istraukia istorija ir suupdatina kad rodytu skola.
    //** not finished **
    public void getSkol(final VolleyCallBack volleyCallBack){
        igSumaArr = new ArrayList<>();
        gabSumaArr = new ArrayList<>();
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
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequestGab);
    }
}