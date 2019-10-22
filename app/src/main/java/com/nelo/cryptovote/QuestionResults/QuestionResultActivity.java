package com.nelo.cryptovote.QuestionResults;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

import java.util.UUID;

public class QuestionResultActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private QuestionResultApiAdapter resultApiAdapter;
    private QuestionResultAdapter adapter;
    private UUID communityId;
    private UUID questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_result);

        initToolbar();

        this.swipeRefreshLayout = findViewById(R.id.questions_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultApiAdapter.get(communityId, questionId, adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.choices);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new QuestionResultAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = this.getIntent();
        questionId = UUID.fromString(intent.getStringExtra("questionId"));
        communityId = UUID.fromString(intent.getStringExtra("communityId"));

        resultApiAdapter = new QuestionResultApiAdapter(this, this.swipeRefreshLayout);
        resultApiAdapter.get(communityId, questionId, adapter);
    }
}
