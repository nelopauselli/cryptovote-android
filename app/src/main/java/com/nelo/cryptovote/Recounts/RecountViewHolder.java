package com.nelo.cryptovote.Recounts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class RecountViewHolder extends RecyclerView.ViewHolder {
    LinearLayout parentLayout, votesLayout;
    TextView choiceLabelTextView, choiceIdTextView;
    EditText votesEditText;

    public RecountViewHolder(View itemView) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.choice_layout);
        choiceLabelTextView = itemView.findViewById(R.id.choice_label);
        choiceIdTextView = itemView.findViewById(R.id.choice_id);

        votesLayout = itemView.findViewById(R.id.choice_votes_layout);
        votesEditText = itemView.findViewById(R.id.choice_votes);
    }
}
