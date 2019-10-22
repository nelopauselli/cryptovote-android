package com.nelo.cryptovote.Recounts;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.Domain.ChoiceRecount;
import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.IssueChoice;
import com.nelo.cryptovote.Domain.Recount;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class RecountDetailActivity extends MyActivity {
    LinearLayout resultsLayout;
    TextView recountPublicKeyTextView, recountSignatureTextView, recountValidTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recount_detail);

        initToolbar();

        resultsLayout = this.findViewById(R.id.recount_results);

        recountPublicKeyTextView = this.findViewById(R.id.recount_publicKey);
        recountSignatureTextView = this.findViewById(R.id.recount_signature);
        recountValidTextView = this.findViewById(R.id.recount_valid);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        Issue issue = (Issue) getIntent().getSerializableExtra("issue");
        if (issue != null) {
            CharSequence urnName = getIntent().getStringExtra("urnName");
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(issue.name + " - " + urnName);
        }

        Recount recount = (Recount) getIntent().getSerializableExtra("recount");
        if (recount != null) {
            ChoiceRecount[] results = recount.results.toArray(new ChoiceRecount[0]);
            Arrays.sort(results, new Comparator<ChoiceRecount>() {
                @Override
                public int compare(ChoiceRecount choiceRecount, ChoiceRecount t1) {
                    return Integer.compare(t1.votes, choiceRecount.votes);
                }
            });

            for (ChoiceRecount result : results) {
                for (IssueChoice choice : issue.choices) {
                    if (choice.id.equals(result.choiceId)) {
                        View child = getLayoutInflater().inflate(R.layout.choice_result_item, null);

                        LinearLayout childLayout = child.findViewById(R.id.recount_result_layout);
                        ByteBuffer bb = ByteBuffer.allocate(4);
                        bb.putInt(choice.color);
                        int red = (255 + bb.get(1)) % 255;
                        int green = (255 + bb.get(2)) % 255;
                        int blue = (255 + bb.get(3)) % 255;
                        childLayout.setBackgroundColor(Color.argb(70, red, green, blue));

                        TextView choiceIdTextView = child.findViewById(R.id.recount_choiceId);
                        choiceIdTextView.setText(result.choiceId.toString());

                        TextView choiceTextTextView = child.findViewById(R.id.choice_text);
                        choiceTextTextView.setText(choice.text);

                        TextView votesTextView = child.findViewById(R.id.recount_votes);
                        votesTextView.setText(String.valueOf(result.votes));

                        resultsLayout.addView(child);
                    }
                }
            }

            Resources resources = getResources();
            recountPublicKeyTextView.setText(String.format("%s: %s", resources.getString(R.string.authority), recount.publicKey));
            recountSignatureTextView.setText(String.format("%s: %s", resources.getString(R.string.sign), recount.signature));
            recountValidTextView.setText(recount.isValid() ? "VALID :D" : "INVALID :(");
        }
    }
}