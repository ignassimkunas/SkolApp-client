package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String url = "http://5.20.217.145:1176/";
    String currentUser;
    String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("currentUser", "Ignas");
        selectedUser = sharedPreferences.getString("selectedUser", "");
        Button addPayment = findViewById(R.id.toPayment);
        final EditText amountView = findViewById(R.id.amount);
        final EditText descriptionView = findViewById(R.id.description);
        queue = Volley.newRequestQueue(this);
        final Intent toMain = new Intent(this, MainActivity.class);

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUser.isEmpty()){
                    Toast.makeText(PaymentActivity.this, "You haven't selected a debt partner.", Toast.LENGTH_SHORT).show();
                }
                else{
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
            }
        });
    }
    public void sendPayment(double ammount, String description, final VolleyCallBackFloatValue volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "add_payment/"+ currentUser + "&" +ammount +'&' + description + "&" + selectedUser  ,
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