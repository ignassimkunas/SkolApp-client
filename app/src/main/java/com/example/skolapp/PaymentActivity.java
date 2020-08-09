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

public class PaymentActivity extends AppCompatActivity {
    RequestQueue queue;
    final String url = "http://94.237.45.148:1176/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
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