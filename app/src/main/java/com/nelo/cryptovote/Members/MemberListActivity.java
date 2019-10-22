package com.nelo.cryptovote.Members;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nelo.cryptovote.Communities.CommunityAddActivity;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

public class MemberListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MemberApiAdapter memberApiAdapter;
    private MemberAdapter adapter;

    private String communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        initToolbar();

        Intent intent = this.getIntent();
        String communityName = intent.getStringExtra("communityName");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(communityName);
        communityId = intent.getStringExtra("communityId");

        final Context context = this;
        this.swipeRefreshLayout = findViewById(R.id.members_refresh);

        memberApiAdapter = new MemberApiAdapter(this, swipeRefreshLayout);

        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                memberApiAdapter.list(communityId, adapter);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.community_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MemberAddActivity.class);
                intent.putExtra("communityId", communityId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.members);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MemberAdapter();
        recyclerView.setAdapter(adapter);

        memberApiAdapter.list(communityId, adapter);
    }
}