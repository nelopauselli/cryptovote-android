package com.nelo.cryptovote.QuestionResults;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class QuestionResultViewHolder extends RecyclerView.ViewHolder{
    LinearLayout parentLayout;
    TextView orderTextView, choiceIdTextView, choiceTextTextView, votesTextView;

    public QuestionResultViewHolder(View itemView) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.choice_layout);
        orderTextView = itemView.findViewById(R.id.choice_order);
        choiceIdTextView = itemView.findViewById(R.id.choice_id);
        choiceTextTextView = itemView.findViewById(R.id.choice_text);
        votesTextView = itemView.findViewById(R.id.recount_votes);
    }
}
