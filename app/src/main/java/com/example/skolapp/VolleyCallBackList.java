package com.example.skolapp;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public interface VolleyCallBackList {
    void onSuccess(JSONArray list) throws JSONException;
}
