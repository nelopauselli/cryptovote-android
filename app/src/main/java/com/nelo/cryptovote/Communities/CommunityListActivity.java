package com.nelo.cryptovote.Communities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

public class CommunityListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;

    private CommunityApiAdapter communityApiAdapter;
    private CommunityAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        initToolbar();

        final Context context = this;

        this.swipeRefreshLayout = findViewById(R.id.communities_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                communityApiAdapter.list(adapter);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.community_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommunityAddActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.communities);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommunityAdapter();
        recyclerView.setAdapter(adapter);

        communityApiAdapter = new CommunityApiAdapter(this, this.swipeRefreshLayout);
        communityApiAdapter.list(adapter);
    }
}