package com.nelo.cryptovote.Votes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.WebApiAdapters.IssueApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.IssueGetListener;
import com.nelo.cryptovote.WebApiAdapters.VoteApiAdapter;

import java.util.UUID;

public class VoteListActivity extends MyActivity {
    private VotesAdapter adapter = new VotesAdapter();
    private VoteApiAdapter voteApiAdapter;
    private IssueApiAdapter issueApiAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UUID issueId;
    private UUID communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_list);

        initToolbar();

        this.swipeRefreshLayout = findViewById(R.id.votes_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                voteApiAdapter.list(issueId, adapter);
            }
        });

        voteApiAdapter = new VoteApiAdapter(this, this.swipeRefreshLayout);
        issueApiAdapter = new IssueApiAdapter(this, this.swipeRefreshLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView rv = findViewById(R.id.issues);

        Intent intent = this.getIntent();
        String issueName = intent.getStringExtra("issueName");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(issueName);

        this.issueId = UUID.fromString(intent.getStringExtra("issueId"));
        this.communityId = UUID.fromString(intent.getStringExtra("communityId"));

        issueApiAdapter.get(communityId, issueId, new IssueGetListener() {
            @Override
            public void onComplete(Issue issue) {
                adapter.setIssue(issue);
                voteApiAdapter.list(issueId, adapter);
            }
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }
}