package com.example.skolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String url = "http://192.168.0.16:1176/users";
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        final Fragment debtFragment = new DebtFragment();
        final Fragment statFragment = new StatFragment();
        final Fragment userFragment = new UserFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, debtFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.debt:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, debtFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.stats:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, statFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.user:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, userFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                }
                return true;
            }
        });

    }
    public void getUsers(final VolleyCallBackList volleyCallBackList){
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        //add query in index.js to get users

        getUsers(new VolleyCallBackList() {
            @Override
            public void onSuccess(JSONArray list) throws JSONException {
                for (int i = 0; i < list.length(); i++){
                    String user = list.getJSONObject(i).getString("user");
                    menu.add(user);
                }
                inflater.inflate(R.menu.users, menu);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }
}