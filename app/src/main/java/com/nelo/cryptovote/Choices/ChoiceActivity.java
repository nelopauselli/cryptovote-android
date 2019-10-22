package com.nelo.cryptovote.Choices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.Domain.Vote;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.QuestionGetListener;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;
import com.nelo.cryptovote.WebApiAdapters.VoteApiAdapter;

import java.util.Date;
import java.util.UUID;

public class ChoiceActivity extends MyActivity{
    private RecyclerView.LayoutManager layoutManager;
    private String tag;

    private Signer signer;
    private QuestionApiAdapter questionApiAdapter;
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

        questionApiAdapter = new QuestionApiAdapter(this, null);
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
        final UUID questionId = UUID.fromString(intent.getStringExtra("questionId"));
        final UUID communityId = UUID.fromString(intent.getStringExtra("communityId"));

        final ChoiceAdapter adapter = new ChoiceAdapter(new ChoiceListener() {
            @Override
            public void onVote(QuestionChoice choice) {
                final Toast working = Toast.makeText(context, "Emitiendo voto...", Toast.LENGTH_LONG);
                working.show();

                try {
                    Vote vote = new Vote();
                    vote.questionId = questionId;
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

        questionApiAdapter.get(communityId, questionId, new QuestionGetListener() {
            @Override
            public void onComplete(Question question) {
                Log.d(tag, "Binding question: " + question.name);

                ActionBar actionBar = getSupportActionBar();
                if(actionBar!=null)
                    actionBar.setTitle(question.name);

                adapter.setEntities(question.choices);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
