package com.nelo.cryptovote.Issues;

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
import com.nelo.cryptovote.WebApiAdapters.IssueApiAdapter;

public class IssueListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private IssueApiAdapter issueApiAdapter;
    private IssueAdapter adapter;
    private String communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_list);

        initToolbar();

        final Context context = this;

        this.swipeRefreshLayout = findViewById(R.id.issues_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                issueApiAdapter.list(communityId, adapter);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.issue_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IssueAddActivity.class);
                intent.putExtra("communityId", communityId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.issues);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new IssueAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = this.getIntent();
        String communityName = intent.getStringExtra("communityName");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(communityName);

        this.communityId = intent.getStringExtra("communityId");

        issueApiAdapter = new IssueApiAdapter(this, this.swipeRefreshLayout);
        issueApiAdapter.list(communityId, adapter);
    }
}