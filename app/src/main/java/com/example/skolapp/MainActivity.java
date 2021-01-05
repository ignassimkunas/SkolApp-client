package com.example.skolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
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

    String url = "http://5.20.217.145:1176/users";
    SharedPreferences sharedPreferences;
    RequestQueue queue;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("currentUser", "");
        intent = new Intent(this, LoginActivity.class);
        if (currentUser.isEmpty()){
            startActivity(intent);
        }
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Logout")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentUser", "");
            editor.apply();
            startActivity(intent);
        }

        return true;
    }
}