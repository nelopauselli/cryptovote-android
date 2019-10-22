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
import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.Questions.QuestionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestionApiAdapter extends ApiAdapter {
    private final String url;
    private final String tag;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public QuestionApiAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.url = server + "/api/question";
        this.tag = getClass().getSimpleName();
    }

    public void list(final String communityId, final QuestionAdapter adapter) {
        String url = this.url + "/" + communityId;
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Log.d(tag, "Conectando con " + url);

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, "Response is: " + response.toString());

                List<Question> questions = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);

                        Question question = new Question();

                        String questionId = item.getString("id");
                        Log.d(tag, "Parsing question " + questionId);

                        question.id = new UUID(
                                new BigInteger(questionId.substring(0, 16), 16).longValue(),
                                new BigInteger(questionId.substring(16), 16).longValue());

                        String communityId = item.getString("communityId");
                        question.communityId = new UUID(
                                new BigInteger(communityId.substring(0, 16), 16).longValue(),
                                new BigInteger(communityId.substring(16), 16).longValue());

                        question.type = (byte) item.getInt("type");
                        question.name = item.getString("name");
                        question.endTime = item.getLong("endTime");
                        question.publicKey = item.getString("publicKey");
                        question.signature = item.getString("signature");

                        questions.add(question);
                    }

                    Log.d(tag, questions.size() + " questions parsed");
                    adapter.setEntities(questions);
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

    public void get(final UUID communityId, final UUID questionId, final QuestionGetListener listener) {
        String url = this.url + "/" + communityId.toString() + "/" + questionId.toString();

        Log.d(tag, "Conectando con " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, "Response is: " + response.toString());

                try {
                    Question question = new Question();

                    String questionId = response.getString("id");
                    Log.d(tag, "Parsing question " + questionId);

                    question.id = new UUID(
                            new BigInteger(questionId.substring(0, 16), 16).longValue(),
                            new BigInteger(questionId.substring(16), 16).longValue());

                    String communityId = response.getString("communityId");
                    question.communityId = new UUID(
                            new BigInteger(communityId.substring(0, 16), 16).longValue(),
                            new BigInteger(communityId.substring(16), 16).longValue());

                    question.name = response.getString("name");
                    question.endTime = response.getLong("endTime");

                    JSONArray choices = response.getJSONArray("choices");
                    Log.d(tag, "Choices: " + choices.length());
                    for (int i = 0; i < choices.length(); i++) {

                        JSONObject item = choices.getJSONObject(i);

                        final QuestionChoice choice = new QuestionChoice();
                        String choiceId = item.getString("id");
                        choice.id = new UUID(
                                new BigInteger(choiceId.substring(0, 16), 16).longValue(),
                                new BigInteger(choiceId.substring(16), 16).longValue());
                        choice.text = item.getString("text");
                        choice.color = item.getInt("color");
                        question.choices.add(choice);
                    }

                    question.publicKey = response.getString("publicKey");
                    question.signature = response.getString("signature");

                    Log.d(tag, "1 questions parsed");
                    listener.onComplete(question);
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

    public void add(final Question question, final RequestListener<Question> listener) {
        Log.d(getClass().getSimpleName(), "Conectando con " + url);

        try {
            final JSONArray choices = new JSONArray();
            for (QuestionChoice choice : question.choices) {
                JSONObject choiceJson = new JSONObject();
                choiceJson.put("id", choice.id);
                choiceJson.put("text", choice.text);
                choiceJson.put("color", choice.color);
                choiceJson.put("guardianAddress", choice.guardianAddress);
                choices.put(choiceJson);
            }
            final JSONObject data = new JSONObject();

            data.put("id", question.id);
            data.put("communityId", question.communityId);
            data.put("name", question.name);
            data.put("type", (int) question.type);
            data.put("endTime", question.endTime);
            data.put("choices", choices);
            data.put("publicKey", question.publicKey);
            data.put("signature", question.signature);

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
                            listener.onComplete(question);
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