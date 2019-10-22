package com.nelo.cryptovote.Urns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.WebApiAdapters.IssueApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.IssueGetListener;
import com.nelo.cryptovote.WebApiAdapters.UrnApiAdapter;

import java.util.UUID;

public class UrnListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;

    private UrnApiAdapter urnApiAdapter;
    private UrnAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UUID communityId, issueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urn_list);

        initToolbar();

        final Context context = this;

        this.swipeRefreshLayout = findViewById(R.id.urns_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                urnApiAdapter.list(issueId, adapter);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.urn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "TODO: Agregar Urna", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final UrnListActivity context = this;

        final RecyclerView recyclerView = findViewById(R.id.urns);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        ActionBar actionBar = getSupportActionBar();
        CharSequence issueName = getIntent().getStringExtra("issueName");
        actionBar.setTitle(issueName);

        Intent intent = this.getIntent();

        communityId = UUID.fromString(intent.getStringExtra("communityId"));
        issueId = UUID.fromString(intent.getStringExtra("issueId"));

        IssueApiAdapter issueApiAdapter = new IssueApiAdapter(this, null);
        issueApiAdapter.get(communityId, issueId, new IssueGetListener() {
            @Override
            public void onComplete(Issue issue) {
                adapter = new UrnAdapter(issue);
                recyclerView.setAdapter(adapter);

                urnApiAdapter = new UrnApiAdapter(context, context.swipeRefreshLayout);
                urnApiAdapter.list(issue.id, adapter);
            }
        });

    }
}