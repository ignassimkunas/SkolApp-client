package com.example.skolapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

public class OnSkolRemoveDialogue extends DialogFragment {
    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Ar skola sumokėta?");
        builder.setPositiveButton("Taip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                //padaugina is dvieju, kad skaitytusi kaip sumoketa skola
                final MainActivity mainActivity = (MainActivity)getActivity();
                assert mainActivity != null;
                sendPayment(Float.parseFloat(mainActivity.skolaView.getText().toString())* -1 * 2, "skolosgrazinimasandroidapp", new VolleyCallBackFloatValue() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mainActivity, "Skola sumokėta", Toast.LENGTH_SHORT).show();
                        mainActivity.updateValue(new VolleyCallBackFloatValue() {
                            @Override
                            public void onSuccess() {

                            }
                        });
                        dialogInterface.dismiss();
                    }
                });
                Toast.makeText(getContext(), "Skola panaikinta", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();

    }
    public void sendPayment(double ammount, String description, final VolleyCallBackFloatValue volleyCallBack){
        final RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        final String url = "http://94.237.45.148:1176/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "add_payment/ignas&" +ammount +'&' + description  ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        volleyCallBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Payment adding unsuccessful", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }
}
