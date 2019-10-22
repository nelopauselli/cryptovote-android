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
import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.IssueChoice;
import com.nelo.cryptovote.Issues.IssueAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IssueApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public IssueApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/issue";
        this.tag = getClass().getSimpleName();
    }

    public void list(final String communityId, final IssueAdapter adapter) {
        String url = this.url + "/" + communityId;
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Log.d(tag, "Conectando con " + url);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, "Response is: " + response.toString());

                List<Issue> issues = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Issue issue = new Issue();

                        String issueId = item.getString("id");
                        Log.d(tag, "Parsing issue " + issueId);

                        issue.id = new UUID(
                                new BigInteger(issueId.substring(0, 16), 16).longValue(),
                                new BigInteger(issueId.substring(16), 16).longValue());

                        String communityId = item.getString("communityId");
                        issue.communityId = new UUID(
                                new BigInteger(communityId.substring(0, 16), 16).longValue(),
                                new BigInteger(communityId.substring(16), 16).longValue());

                        issue.type = (byte) item.getInt("type");
                        issue.name = item.getString("name");
                        issue.endTime = item.getLong("endTime");
                        issue.publicKey = item.getString("publicKey");
                        issue.signature = item.getString("signature");

                        issues.add(issue);
                    }

                    Log.d(tag, issues.size() + " issues parsed");
                    adapter.setEntities(issues);
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error parsearndo respuesta", e);
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(), "That didn't work!", error);
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void get(final UUID communityId, final UUID issueId, final IssueGetListener listener) {
        String url = this.url + "/" + communityId.toString() + "/" + issueId.toString();

        Log.d(tag, "Conectando con " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, "Response is: " + response.toString());

                try {
                    Issue issue = new Issue();

                    String issueId = response.getString("id");
                    Log.d(tag, "Parsing issue " + issueId);

                    issue.id = new UUID(
                            new BigInteger(issueId.substring(0, 16), 16).longValue(),
                            new BigInteger(issueId.substring(16), 16).longValue());

                    String communityId = response.getString("communityId");
                    issue.communityId = new UUID(
                            new BigInteger(communityId.substring(0, 16), 16).longValue(),
                            new BigInteger(communityId.substring(16), 16).longValue());

                    issue.name = response.getString("name");
                    issue.endTime = response.getLong("endTime");

                    JSONArray choices = response.getJSONArray("choices");
                    Log.d(tag, "Choices: " + choices.length());
                    for (int i = 0; i < choices.length(); i++) {

                        JSONObject item = choices.getJSONObject(i);

                        final IssueChoice choice = new IssueChoice();
                        String choiceId = item.getString("id");
                        choice.id = new UUID(
                                new BigInteger(choiceId.substring(0, 16), 16).longValue(),
                                new BigInteger(choiceId.substring(16), 16).longValue());
                        choice.text = item.getString("text");
                        choice.color = item.getInt("color");
                        issue.choices.add(choice);
                    }

                    issue.publicKey = response.getString("publicKey");
                    issue.signature = response.getString("signature");

                    Log.d(tag, "1 issues parsed");
                    listener.onComplete(issue);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error parseando respuesta", e);
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

    public void add(final Issue issue, final RequestListener<Issue> listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONArray choices = new JSONArray();
            for (IssueChoice choice : issue.choices) {
                JSONObject choiceJson = new JSONObject();
                choiceJson.put("id", choice.id);
                choiceJson.put("text", choice.text);
                choiceJson.put("color", choice.color);
                choiceJson.put("guardianAddress", choice.guardianAddress);
                choices.put(choiceJson);
            }
            final JSONObject data = new JSONObject();

            data.put("id", issue.id);
            data.put("communityId", issue.communityId);
            data.put("name", issue.name);
            data.put("type", (int) issue.type);
            data.put("endTime", issue.endTime);
            data.put("choices", choices);
            data.put("publicKey", issue.publicKey);
            data.put("signature", issue.signature);

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
                            listener.onComplete(issue);
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
                    params.put("Content-Length", String.valueOf(data.toString().getBytes().length));

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