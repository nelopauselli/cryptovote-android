package com.nelo.cryptovote.WebApiAdapters;

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
import com.nelo.cryptovote.Domain.Vote;
import com.nelo.cryptovote.Votes.VotesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VoteApiAdapter extends ApiAdapter {
    private final String url;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public VoteApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/vote";
    }

    public void list(final UUID questionId, final VotesAdapter adapter) {
        String url = this.url + "/" + questionId.toString();
        Log.d(getClass().getSimpleName(), "Conectando con " + url);
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(getClass().getSimpleName(), "Response is: " + response.toString());

                List<Vote> votes = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Vote vote = new Vote();
                        String questionId = item.getString("questionId");
                        vote.questionId = new UUID(
                                new BigInteger(questionId.substring(0, 16), 16).longValue(),
                                new BigInteger(questionId.substring(16), 16).longValue());

                        String choiceId = item.getString("choiceId");
                        vote.choiceId = new UUID(
                                new BigInteger(choiceId.substring(0, 16), 16).longValue(),
                                new BigInteger(choiceId.substring(16), 16).longValue());

                        vote.time = item.getLong("time");
                        vote.publicKey = item.getString("publicKey");
                        vote.signature = item.getString("signature");

                        votes.add(vote);
                    }
                    adapter.setEntities(votes);
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error parsearndo respuesta", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(), "That didn't work!", error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("questionId", questionId.toString());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void send(final Vote vote, final RequestListener listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONObject data = new JSONObject();

            data.put("questionId", vote.questionId);
            data.put("choiceId", vote.choiceId);
            data.put("time", vote.time);
            data.put("publicKey", vote.publicKey);
            data.put("signature", vote.signature);

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
                            listener.onComplete(vote);
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