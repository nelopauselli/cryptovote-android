package com.nelo.cryptovote.Choices;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class ChoiceViewHolder extends RecyclerView.ViewHolder{
    LinearLayout parentLayout;
    TextView choiceIdTextView, choiceNameTextView;
    AppCompatImageButton voteButton;

    public ChoiceViewHolder(View itemView) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.choice_layout);
        choiceIdTextView = itemView.findViewById(R.id.choice_id);
        choiceNameTextView = itemView.findViewById(R.id.choice_name);
        voteButton = itemView.findViewById(R.id.choice_vote);
    }
}
