package com.nelo.cryptovote.Questions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;

public class QuestionListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private QuestionApiAdapter questionApiAdapter;
    private QuestionAdapter adapter;
    private String communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        initToolbar();

        final Context context = this;

        this.swipeRefreshLayout = findViewById(R.id.questions_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                questionApiAdapter.list(communityId, adapter);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.question_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionAddActivity.class);
                intent.putExtra("communityId", communityId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.questions);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new QuestionAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = this.getIntent();
        String communityName = intent.getStringExtra("communityName");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(communityName);

        this.communityId = intent.getStringExtra("communityId");

        questionApiAdapter = new QuestionApiAdapter(this, this.swipeRefreshLayout);
        questionApiAdapter.list(communityId, adapter);
    }
}