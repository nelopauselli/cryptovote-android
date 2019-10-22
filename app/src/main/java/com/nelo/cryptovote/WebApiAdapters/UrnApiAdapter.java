package com.nelo.cryptovote.WebApiAdapters;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nelo.cryptovote.ApiAdapter;
import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.IssueChoice;
import com.nelo.cryptovote.Domain.Urn;
import com.nelo.cryptovote.Issues.IssueAdapter;
import com.nelo.cryptovote.Urns.UrnAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UrnApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public UrnApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/urn";
        this.tag = getClass().getSimpleName();
    }

    public void list(final UUID issueId, final UrnAdapter adapter) {
        String url = this.url + "/" + issueId.toString();
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Log.d(tag, "Conectando con " + url);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, "Response is: " + response.toString());

                List<Urn> urns = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Urn urn = new Urn();

                        String urnId = item.getString("id");
                        Log.d(tag, "Parsing issue " + issueId);

                        urn.id = new UUID(
                                new BigInteger(urnId.substring(0, 16), 16).longValue(),
                                new BigInteger(urnId.substring(16), 16).longValue());

                        String issueId = item.getString("issueId");
                        Log.d(tag, "Parsing issue " + issueId);

                        urn.issueId = new UUID(
                                new BigInteger(issueId.substring(0, 16), 16).longValue(),
                                new BigInteger(issueId.substring(16), 16).longValue());

                        urn.name = item.getString("name");

                        JSONArray authorities = item.getJSONArray("authorities");
                        for (int j = 0; j < authorities.length(); j++) {
                            byte[] authority = Base58.decode(authorities.getString(j));
                            urn.authorities.add(authority);
                        }

                        urn.publicKey = item.getString("publicKey");
                        urn.signature = item.getString("signature");

                        urns.add(urn);
                    }

                    Log.d(tag, urns.size() + " issues parsed");
                    adapter.setEntities(urns);
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
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void get(final String communityId, final String issueId, final IssueGetListener listener) {
        String url = this.url + "/" + communityId + "/" + issueId;

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
}