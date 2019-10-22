package com.nelo.cryptovote.Recounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.ChoiceRecount;
import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.Recount;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.QuestionGetListener;
import com.nelo.cryptovote.WebApiAdapters.RecountApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.util.UUID;

public class RecountAddActivity extends MyActivity {
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecountAdapter adapter;
    private String tag;

    private UUID communityId, questionId, urnId;

    private Signer signer;
    private QuestionApiAdapter questionApiAdapter;
    private RecountApiAdapter recountApiAdapter;

    private FloatingActionButton sendButton;

    public RecountAddActivity() {
        tag = getClass().getSimpleName();
        signer = new Signer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recount);

        final Context context = this;

        initToolbar();

        sendButton = findViewById(R.id.recount_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Toast working = Toast.makeText(context, "Emitiendo recuento...", Toast.LENGTH_LONG);
                working.show();

                try {
                    Recount recount = new Recount();
                    recount.id = UUID.randomUUID();
                    recount.urnId = urnId;

                    Log.d("RecountAddActivity", "choices: " + adapter.getItemCount());

                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        Log.d("RecountAddActivity", "Choice: " + i);
                        View child = recyclerView.getLayoutManager().findViewByPosition(i);
                        if (child == null) continue;

                        EditText votesEditText = child.findViewById(R.id.choice_votes);
                        String votesAsString = votesEditText.getText().toString();
                        Log.d("RecountAddActivity", "votes: " + votesAsString);

                        TextView choiceIdTextView = child.findViewById(R.id.choice_id);
                        String choiceIdAsString = choiceIdTextView.getText().toString();
                        Log.d("RecountAddActivity", "choiceId: " + choiceIdAsString);

                        ChoiceRecount choiceRecount = new ChoiceRecount();
                        choiceRecount.choiceId = UUID.fromString(choiceIdAsString);
                        choiceRecount.votes = Integer.parseInt(votesAsString);

                        recount.results.add(choiceRecount);
                    }

                    Log.i("RecountAddActivity", "raw: " + Base58.encode(recount.getData()));

                    signer.sign(recount);

                    recountApiAdapter.send(recount, new RequestListener<Recount>() {
                        @Override
                        public void onComplete(Recount recount) {
                            working.cancel();
                            Toast.makeText(context, "Recuento emitido! :)", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int statusCode) {
                            working.cancel();
                            Toast.makeText(context, "Error " + statusCode + " emitiendo el recuento :(", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception ex) {
                    working.cancel();
                    Log.e("RecountAdd", ex.getMessage(), ex);
                    Toast.makeText(context, "Error en enviando recuento: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        questionApiAdapter = new QuestionApiAdapter(this, null);
        recountApiAdapter = new RecountApiAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView = findViewById(R.id.choices);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = this.getIntent();
        communityId = UUID.fromString(intent.getStringExtra("communityId"));
        questionId = UUID.fromString(intent.getStringExtra("questionId"));
        urnId = UUID.fromString(intent.getStringExtra("urnId"));

        ActionBar actionBar = getSupportActionBar();
        CharSequence questionName = getIntent().getStringExtra("questionName");
        CharSequence urnName = getIntent().getStringExtra("urnName");
        actionBar.setTitle(questionName + " - " + urnName);

        adapter = new RecountAdapter();
        recyclerView.setAdapter(adapter);

        questionApiAdapter.get(communityId, questionId, new QuestionGetListener() {
            @Override
            public void onComplete(Question question) {
                Log.d(tag, "Binding question: " + question.name);

                adapter.setEntities(question.choices);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
