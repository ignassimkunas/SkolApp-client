package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.example.skolapp.MainActivity.getCurrentSsid;

public class PaymentActivity extends AppCompatActivity {
    RequestQueue queue;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if (getCurrentSsid(this).equals("\"GabAndIg5Ghz\"") || getCurrentSsid(this).equals("\"GabAndIg24Ghz\"") || getCurrentSsid(this).equals("\"GabAndIg\"")){
            url = "http://192.168.0.45:1176/";
        }
        else{
            url = "http://5.20.217.145:1176/";
        }
        Button addPayment = findViewById(R.id.toPayment);
        final EditText amountView = findViewById(R.id.amount);
        final EditText descriptionView = findViewById(R.id.description);
        queue = Volley.newRequestQueue(this);
        final Intent toMain = new Intent(this, MainActivity.class);
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Double amount = Double.parseDouble(amountView.getText().toString());
                String description = descriptionView.getText().toString();
                sendPayment(amount, description, new VolleyCallBackFloatValue(){
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PaymentActivity.this, "Added payment of " + amount + " euros", Toast.LENGTH_SHORT).show();
                        startActivity(toMain);
                    }
                });
            }
        });
    }
    public void sendPayment(double ammount, String description, final VolleyCallBackFloatValue volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "add_payment/Ignas&" +ammount +'&' + description  ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        volleyCallBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "Payment adding unsuccessful", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }
}