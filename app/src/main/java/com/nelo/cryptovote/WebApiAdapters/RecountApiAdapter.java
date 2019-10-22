package com.nelo.cryptovote.WebApiAdapters;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nelo.cryptovote.ApiAdapter;
import com.nelo.cryptovote.Domain.ChoiceRecount;
import com.nelo.cryptovote.Domain.Recount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecountApiAdapter extends ApiAdapter {
    private final String url;
    private Context context;

    public RecountApiAdapter(Context context) {
        this.context = context;
        this.url = server + "/api/recount";
    }

    public void list(final UUID urnId, final RequestListener<Recount> adapter) {
        String url = this.url + "/" + urnId.toString();
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(getClass().getSimpleName(), "Response is: " + response.toString());

                Recount recount = new Recount();

                String urnId = null;
                try {
                    urnId = response.getString("urnId");
                    recount.urnId = new UUID(
                            new BigInteger(urnId.substring(0, 16), 16).longValue(),
                            new BigInteger(urnId.substring(16), 16).longValue());

                    String id = response.getString("id");
                    recount.id = new UUID(
                            new BigInteger(id.substring(0, 16), 16).longValue(),
                            new BigInteger(id.substring(16), 16).longValue());

                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {

                        JSONObject item = results.getJSONObject(i);

                        final ChoiceRecount result = new ChoiceRecount();
                        String choiceId = item.getString("choiceId");
                        result.choiceId = new UUID(
                                new BigInteger(choiceId.substring(0, 16), 16).longValue(),
                                new BigInteger(choiceId.substring(16), 16).longValue());
                        result.votes = item.getInt("votes");
                        recount.results.add(result);
                    }
                    recount.publicKey = response.getString("publicKey");
                    recount.signature = response.getString("signature");

                    adapter.onComplete(recount);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(), "That didn't work!", error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void send(final Recount recount, final RequestListener listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONArray results = new JSONArray();
            for (ChoiceRecount result : recount.results) {
                final JSONObject dataResult = new JSONObject();

                dataResult.put("choiceId", result.choiceId);
                dataResult.put("votes", result.votes);

                results.put(dataResult);
            }

            final JSONObject data = new JSONObject();

            data.put("id", recount.id);
            data.put("urnId", recount.urnId);
            data.put("results", results);
            data.put("publicKey", recount.publicKey);
            data.put("signature", recount.signature);

            Log.d(getClass().getSimpleName(), "Sending: " + data.toString());

            // Request a string response from the provided URL.
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(getClass().getSimpleName(), "Response is: " + response);
                            listener.onComplete(recount);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(getClass().getSimpleName(), "That didn't work!", error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json; charset=utf-8");
                    params.put("Content-Length", String.valueOf(data.toString().length()));

                    Log.d("getHeaders", params.toString());
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "Error enviando voto", e);
        }
    }
}