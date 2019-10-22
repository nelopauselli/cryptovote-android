package com.nelo.cryptovote.Votes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.QuestionGetListener;
import com.nelo.cryptovote.WebApiAdapters.VoteApiAdapter;

import java.util.UUID;

public class VoteListActivity extends MyActivity {
    private VotesAdapter adapter = new VotesAdapter();
    private VoteApiAdapter voteApiAdapter;
    private QuestionApiAdapter questionApiAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UUID questionId;
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
                voteApiAdapter.list(questionId, adapter);
            }
        });

        voteApiAdapter = new VoteApiAdapter(this, this.swipeRefreshLayout);
        questionApiAdapter = new QuestionApiAdapter(this, this.swipeRefreshLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView rv = findViewById(R.id.questions);

        Intent intent = this.getIntent();
        String questionName = intent.getStringExtra("questionName");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(questionName);

        this.questionId = UUID.fromString(intent.getStringExtra("questionId"));
        this.communityId = UUID.fromString(intent.getStringExtra("communityId"));

        questionApiAdapter.get(communityId, questionId, new QuestionGetListener() {
            @Override
            public void onComplete(Question question) {
                adapter.setQuestion(question);
                voteApiAdapter.list(questionId, adapter);
            }
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }
}