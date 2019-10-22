package com.nelo.cryptovote.Votes;

import android.os.Bundle;
import android.widget.TextView;

import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.Domain.Vote;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

import java.util.Date;

public class VoteDetailActivity extends MyActivity {
    TextView questionNameTextView,
            choiceIdTextView, choiceTextTextView,
            voteTimeTextView, votePublicKeyTextView, voteSignatureTextView, voteValidTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);

        initToolbar();

        questionNameTextView = this.findViewById(R.id.question_name);

        choiceTextTextView = this.findViewById(R.id.choice_text);
        choiceIdTextView = this.findViewById(R.id.vote_choiceId);

        voteTimeTextView = this.findViewById(R.id.vote_time);
        votePublicKeyTextView = this.findViewById(R.id.vote_publicKey);
        voteSignatureTextView = this.findViewById(R.id.vote_signature);
        voteValidTextView = this.findViewById(R.id.vote_valid);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Question question = (Question) getIntent().getSerializableExtra("question");
        if (question != null) {
            questionNameTextView.setText(question.name);
        }

        Vote vote = (Vote) getIntent().getSerializableExtra("vote");
        if (vote != null) {
            choiceIdTextView.setText(vote.choiceId.toString());
            for (QuestionChoice choice : question.choices) {
                if (choice.id.equals(vote.choiceId)) {
                    choiceTextTextView.setText(choice.text);
                    break;
                }
            }

            voteTimeTextView.setText(String.valueOf(new Date(vote.time).toString()));
            votePublicKeyTextView.setText(vote.publicKey);
            voteSignatureTextView.setText(vote.signature);
            voteValidTextView.setText(vote.isValid() ? "VALID :D" : "INVALID :(");
        }
    }
}