package com.nelo.cryptovote.IssueResults;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

import java.util.UUID;

public class IssueResultActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private IssueResultApiAdapter resultApiAdapter;
    private IssueResultAdapter adapter;
    private UUID communityId;
    private UUID issueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_result);

        initToolbar();

        this.swipeRefreshLayout = findViewById(R.id.issues_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultApiAdapter.get(communityId, issueId, adapter);
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

        adapter = new IssueResultAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = this.getIntent();
        issueId = UUID.fromString(intent.getStringExtra("issueId"));
        communityId = UUID.fromString(intent.getStringExtra("communityId"));

        resultApiAdapter = new IssueResultApiAdapter(this, this.swipeRefreshLayout);
        resultApiAdapter.get(communityId, issueId, adapter);
    }
}
