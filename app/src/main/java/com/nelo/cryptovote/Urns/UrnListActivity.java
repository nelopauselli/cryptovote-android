package com.nelo.cryptovote.Urns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.QuestionGetListener;
import com.nelo.cryptovote.WebApiAdapters.UrnApiAdapter;

import java.util.UUID;

public class UrnListActivity extends MyActivity {
    private RecyclerView.LayoutManager layoutManager;

    private UrnApiAdapter urnApiAdapter;
    private UrnAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UUID communityId, questionId;

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
                urnApiAdapter.list(questionId, adapter);
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
        CharSequence questionName = getIntent().getStringExtra("questionName");
        actionBar.setTitle(questionName);

        Intent intent = this.getIntent();

        communityId = UUID.fromString(intent.getStringExtra("communityId"));
        questionId = UUID.fromString(intent.getStringExtra("questionId"));

        QuestionApiAdapter questionApiAdapter = new QuestionApiAdapter(this, null);
        questionApiAdapter.get(communityId, questionId, new QuestionGetListener() {
            @Override
            public void onComplete(Question question) {
                adapter = new UrnAdapter(question);
                recyclerView.setAdapter(adapter);

                urnApiAdapter = new UrnApiAdapter(context, context.swipeRefreshLayout);
                urnApiAdapter.list(question.id, adapter);
            }
        });

    }
}