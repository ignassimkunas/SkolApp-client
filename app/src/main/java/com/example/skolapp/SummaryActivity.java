package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SummaryActivity extends AppCompatActivity {
    RequestQueue queue;
    //add url for results on current user
    String currentUser;

    String url;

    TableLayout tableLayout;
    final int textSize = 13;
    final int textSizeHeader = 24;
    final int padding = 16;
    DateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("currentUser", "Ignas");
        url = "http://192.168.0.16:1176/by_name/" +currentUser;
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_summary);
        tableLayout = findViewById(R.id.tableLayout);
        format = new SimpleDateFormat("YYYY-MM-DD");
        getPayments(new VolleyCallBackStringValue() {
            @Override
            public void onSuccess(String value){
                try{
                    JSONArray array = new JSONArray(value);
                    populateTable(array);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
    //pakeist kad visus paymentus gautu
    public void getPayments(final VolleyCallBackStringValue volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        volleyCallBack.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SummaryActivity.this, "Connection to server failed", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }
    private void populateTable(JSONArray data){
        TableRow row = new TableRow(this);
        Typeface face = Typeface.createFromAsset(getAssets(), "solway_medium.ttf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(), "solway_bold.ttf");
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER);
        TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(textSizeHeader);
        tv2.setText("User");
        tv2.setTypeface(boldFace);
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(padding,5,padding,padding);
        row.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextSize(textSizeHeader);
        tv3.setText("Amount");
        tv3.setTypeface(boldFace);
        tv3.setPadding(padding,5,padding,padding);
        tv3.setTextColor(Color.WHITE);
        row.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextSize(textSizeHeader);
        tv4.setText("Descr.");
        tv4.setTypeface(boldFace);
        tv4.setTextColor(Color.WHITE);
        tv4.setPadding(padding,5,padding,padding);
        row.addView(tv4);

        TextView tv1 = new TextView(this);
        tv1.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(textSizeHeader);
        tv1.setText("Date");
        tv1.setTypeface(boldFace);
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(padding,5,padding,padding);
        row.addView(tv1);

        tableLayout.addView(row);

        for (int i = 0; i < data.length(); i++){
            try{
                TableRow dataRow = new TableRow(this);
                dataRow.setGravity(Gravity.CENTER);
                dataRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                TextView user = new TextView(this);
                user.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                user.setGravity(Gravity.CENTER);
                user.setTextSize(textSize);
                user.setTypeface(face);
                user.setTextColor(Color.WHITE);
                user.setPadding(padding,padding,padding,padding);
                user.setText(data.getJSONObject(i).getString("user"));
                dataRow.addView(user);

                TextView amount = new TextView(this);
                amount.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                amount.setGravity(Gravity.CENTER);
                amount.setTextSize(textSize);
                amount.setTypeface(face);
                amount.setTextColor(Color.WHITE);
                amount.setPadding(padding,padding,padding,padding);
                amount.setText(data.getJSONObject(i).getDouble("ammount") + "€");
                dataRow.addView(amount);

                TextView description = new TextView(this);
                description.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                description.setGravity(Gravity.CENTER);
                description.setTextSize(textSize);
                description.setTypeface(face);
                description.setTextColor(Color.WHITE);
                description.setPadding(padding,padding,padding,padding);
                description.setText(data.getJSONObject(i).getString("description"));
                dataRow.addView(description);

                TextView date = new TextView(this);
                date.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                date.setGravity(Gravity.CENTER);
                date.setTextSize(textSize);
                date.setTypeface(face);
                date.setTextColor(Color.WHITE);
                date.setPadding(padding,padding,padding,padding);
                date.setText(data.getJSONObject(i).getString("date").substring(0, 10));
                dataRow.addView(date);
                tableLayout.addView(dataRow);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}