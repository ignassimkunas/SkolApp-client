package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public void createUser(final String username){
        final Intent intent = new Intent(this, LoginActivity.class);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://5.20.217.145:1176/create_user";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this, "User created successfully. You can now login.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
            }

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("user", username);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText repeatPassword = findViewById(R.id.repeat_pass);
        Button register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repeatPass = repeatPassword.getText().toString();

                if (!user.isEmpty() && !pass.isEmpty() && !repeatPass.isEmpty()){
                    if (pass.equals(repeatPass)){
                        createUser(user);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Please fill out all of the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}