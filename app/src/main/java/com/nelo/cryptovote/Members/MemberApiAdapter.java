package com.nelo.cryptovote.Members;

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
import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.Community;
import com.nelo.cryptovote.Domain.Member;
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

public class MemberApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MemberApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/member";
        this.tag = getClass().getSimpleName();
    }

    public void list(final String communityId, final MemberAdapter adapter) {
        String url = this.url + "/" + communityId;
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Log.d(tag, "Conectando con " + url);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, "Response is: " + response.toString());

                List<Member> members = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Member member = new Member();

                        String memberId = item.getString("id");
                        Log.d(tag, "Parsing member " + memberId);

                        member.id = new UUID(
                                new BigInteger(memberId.substring(0, 16), 16).longValue(),
                                new BigInteger(memberId.substring(16), 16).longValue());

                        String communityId = item.getString("communityId");
                        member.communityId = new UUID(
                                new BigInteger(communityId.substring(0, 16), 16).longValue(),
                                new BigInteger(communityId.substring(16), 16).longValue());

                        member.name = item.getString("name");
                        member.address = item.getString("address");
                        member.publicKey = item.getString("publicKey");
                        member.signature = item.getString("signature");

                        members.add(member);
                    }

                    Log.d(tag, members.size() + " members parsed");
                    adapter.setEntities(members);
                    adapter.notifyDataSetChanged();
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    Log.e(getClass().getSimpleName(), "Error parsearndo respuesta", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                Log.e(getClass().getSimpleName(), "That didn't work!", error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void add(final Member member, final RequestListener<Member> listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONObject data = new JSONObject();

            data.put("id", member.id);
            data.put("communityId", member.communityId);
            data.put("name", member.name);
            data.put("address", member.address);
            data.put("publicKey", member.publicKey);
            data.put("signature", member.signature);

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
                            listener.onComplete(member);
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