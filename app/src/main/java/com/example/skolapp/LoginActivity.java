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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public void getUsers(final VolleyCallBackList volleyCallBackList){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://5.20.217.145:1176/users";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            //pass JSONArray to on success and get user list from that
                            volleyCallBackList.onSuccess(array);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final Intent intent = new Intent(this, MainActivity.class);
        final Intent toRegister = new Intent(this, RegisterActivity.class);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toRegister);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsers(new VolleyCallBackList() {
                    @Override
                    public void onSuccess(JSONArray list) throws JSONException {
                        if (password.getText().toString().isEmpty()){
                            Toast.makeText(LoginActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ArrayList<String> users = new ArrayList<>();
                            for (int i = 0; i < list.length(); i++){
                                users.add(list.getJSONObject(i).getString("user"));
                            }
                            String user = username.getText().toString();
                            if (users.contains(user)){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("currentUser", user);
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Hello " + user, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });


    }
}