package com.java.liyonghui;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FetchNews extends AsyncTask<JSONObject, Integer, List<JSONObject>> {
    private Runnable callback;

    FetchNews(Runnable callback) {
        this.callback = callback;
    }

    @Override
    protected List<JSONObject> doInBackground(JSONObject... jsonObjects) {
        final JSONObject response = jsonObjects[0];
        List<JSONObject> result = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                result.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(final List<JSONObject> allNews) {

        if (callback != null) {
            callback.run();
        }
    }

}
