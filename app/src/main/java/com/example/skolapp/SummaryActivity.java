package com.example.skolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
    final String url = "http://94.237.45.148:1176/";
    TableLayout tableLayout;
    final int textSize = 18;
    final int textSizeHeader = 23;
    final int paddingStart = 29;
    final int paddingTop = 10;
    DateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(textSizeHeader);
        tv2.setText("User");
        tv2.setTypeface(face);
        tv2.setTextColor(Color.WHITE);
        tv2.setPaddingRelative(paddingStart, 0, 0, 0);
        row.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextSize(textSizeHeader);
        tv3.setText("Amount");
        tv3.setTypeface(face);
        tv3.setTextColor(Color.WHITE);
        tv3.setPaddingRelative(paddingStart, 0, 0, 0);
        row.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextSize(textSizeHeader);
        tv4.setText("Descr.");
        tv4.setTypeface(face);
        tv4.setTextColor(Color.WHITE);
        tv4.setPaddingRelative(paddingStart, 0, 0, 0);
        row.addView(tv4);

        TextView tv1 = new TextView(this);
        tv1.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(textSizeHeader);
        tv1.setText("Date");
        tv1.setTypeface(face);
        tv1.setTextColor(Color.WHITE);
        tv1.setPaddingRelative(paddingStart, 0, 0, 0);
        row.addView(tv1);

        tableLayout.addView(row);

        for (int i = 0; i < data.length(); i++){
            try{
                TableRow dataRow = new TableRow(this);
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
                user.setText(data.getJSONObject(i).getString("user"));
                user.setPaddingRelative(paddingStart, paddingTop, 0, 0);
                dataRow.addView(user);

                TextView amount = new TextView(this);
                amount.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                amount.setGravity(Gravity.CENTER);
                amount.setTextSize(textSize);
                amount.setTypeface(face);
                amount.setTextColor(Color.WHITE);
                amount.setText(Double.toString(data.getJSONObject(i).getDouble("ammount")));
                amount.setPaddingRelative(paddingStart, paddingTop, 0, 0);
                dataRow.addView(amount);

                TextView description = new TextView(this);
                description.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                description.setGravity(Gravity.START);
                description.setTextSize(textSize);
                description.setTypeface(face);
                description.setTextColor(Color.WHITE);
                description.setText(data.getJSONObject(i).getString("description"));
                description.setPaddingRelative(paddingStart, paddingTop, 0, 0);
                dataRow.addView(description);

                TextView date = new TextView(this);
                date.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                date.setGravity(Gravity.CENTER);
                date.setTextSize(textSize);
                date.setTypeface(face);
                date.setTextColor(Color.WHITE);
                date.setText(data.getJSONObject(i).getString("date").substring(0, 10));
                date.setPaddingRelative(paddingStart, paddingTop, 0, 0);
                dataRow.addView(date);
                tableLayout.addView(dataRow);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}