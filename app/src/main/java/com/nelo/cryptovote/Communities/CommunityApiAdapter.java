package com.nelo.cryptovote.Communities;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nelo.cryptovote.ApiAdapter;
import com.nelo.cryptovote.Domain.Community;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommunityApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    public CommunityApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.url = server + "/api/community";

        this.tag = getClass().getSimpleName();
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void list(final CommunityAdapter adapter) {
        Log.d(tag, "Conectando con " + url);
        swipeRefreshLayout.setRefreshing(true);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, "Response is: " + response.toString());

                List<Community> communities = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Community community = new Community();

                        String communityId = item.getString("id");
                        Log.d(tag, "Parsing community " + communityId);

                        community.id = new UUID(
                                new BigInteger(communityId.substring(0, 16), 16).longValue(),
                                new BigInteger(communityId.substring(16), 16).longValue());

                        community.name = item.getString("name");
                        community.createAt = item.getLong("createAt");
                        community.publicKey = item.getString("publicKey");
                        community.signature = item.getString("signature");

                        communities.add(community);
                    }

                    Log.d(tag, communities.size() + " communities parsed");
                    adapter.setEntities(communities);

                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error parsearndo respuesta", e);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(), "That didn't work!", error);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void add(final Community community, final RequestListener<Community> listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONObject data = new JSONObject();

            data.put("id", community.id);
            data.put("name", community.name);
            data.put("createAt", community.createAt);
            data.put("publicKey", community.publicKey);
            data.put("signature", community.signature);

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
                            listener.onComplete(community);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(getClass().getSimpleName(), "That didn't work!", error);
                    int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                    listener.onError(statusCode);
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
            Log.e(getClass().getSimpleName(), "Error creando organización", e);
        }
    }
}