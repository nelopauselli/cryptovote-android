package com.nelo.cryptovote.IssueResults;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nelo.cryptovote.ApiAdapter;
import com.nelo.cryptovote.Domain.ChoiceResult;
import com.nelo.cryptovote.Domain.IssueResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.UUID;

public class IssueResultApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public IssueResultApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/result";
        this.tag = getClass().getSimpleName();
    }

    public void get(final UUID communityId, final UUID issueId, final IssueResultAdapter adapter) {
        String url = this.url + "/" + communityId.toString() + "/" + issueId.toString();
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Log.d(tag, "Conectando con " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, "Response is: " + response.toString());

                try {
                    IssueResult result = new IssueResult();

                    String issueId = response.getString("issueId");
                    result.issueId = new UUID(new BigInteger(issueId.substring(0, 16), 16).longValue(), new BigInteger(issueId.substring(16), 16).longValue());

                    result.type = (byte) response.getInt("type");

                    JSONArray choices = response.getJSONArray("choices");
                    Log.d(tag, "Choices: " + choices.length());
                    for (int i = 0; i < choices.length(); i++) {

                        JSONObject item = choices.getJSONObject(i);

                        final ChoiceResult choice = new ChoiceResult();
                        String choiceId = item.getString("choiceId");
                        choice.choiceId = new UUID(new BigInteger(choiceId.substring(0, 16), 16).longValue(), new BigInteger(choiceId.substring(16), 16).longValue());
                        choice.text = item.getString("text");
                        choice.color = item.getInt("color");
                        choice.votes = item.getLong("votes");
                        result.choices.add(choice);
                    }

                    Log.d(tag, "1 result parsed");
                    adapter.setEntities(result);
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
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