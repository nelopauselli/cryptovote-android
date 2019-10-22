package com.nelo.cryptovote.Choices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.IssueChoice;
import com.nelo.cryptovote.Domain.Vote;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.IssueApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.IssueGetListener;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;
import com.nelo.cryptovote.WebApiAdapters.VoteApiAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

public class ChoiceActivity extends MyActivity{
    private RecyclerView.LayoutManager layoutManager;
    private String tag;

    private Signer signer;
    private IssueApiAdapter issueApiAdapter;
    private VoteApiAdapter voteApiAdapter;

    public ChoiceActivity() {
        tag = getClass().getSimpleName();

        signer = new Signer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        initToolbar();

        issueApiAdapter = new IssueApiAdapter(this, null);
        voteApiAdapter = new VoteApiAdapter(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.choices);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        final Context context = this;

        Intent intent = this.getIntent();
        final UUID issueId = UUID.fromString(intent.getStringExtra("issueId"));
        final UUID communityId = UUID.fromString(intent.getStringExtra("communityId"));

        final ChoiceAdapter adapter = new ChoiceAdapter(new ChoiceListener() {
            @Override
            public void onVote(IssueChoice choice) {
                final Toast working = Toast.makeText(context, "Emitiendo voto...", Toast.LENGTH_LONG);
                working.show();

                try {
                    Vote vote = new Vote();
                    vote.issueId = issueId;
                    vote.choiceId = choice.id;
                    vote.time = new Date().getTime();

                    signer.sign(vote);

                    voteApiAdapter.send(vote, new RequestListener<Vote>() {
                        @Override
                        public void onComplete(Vote vote) {
                            working.cancel();
                            Toast.makeText(context, "Voto emitido! :)", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int statusCode) {
                            working.cancel();
                            Toast.makeText(context, "Error " + statusCode + " emitiendo el voto :(", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (Exception ex){
                    working.cancel();
                    Log.e("ChoiceAdd", ex.getMessage(), ex);
                    Toast.makeText(context, "Error en enviando voto: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        issueApiAdapter.get(communityId, issueId, new IssueGetListener() {
            @Override
            public void onComplete(Issue issue) {
                Log.d(tag, "Binding issue: " + issue.name);

                ActionBar actionBar = getSupportActionBar();
                if(actionBar!=null)
                    actionBar.setTitle(issue.name);

                adapter.setEntities(issue.choices);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
