package com.nelo.cryptovote.Votes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class VoteViewHolder extends RecyclerView.ViewHolder{
    TextView publicKeyTextView, signatureTextView;
    TextView choiceTextTextView;
    TextView choiceIdTextView;
    TextView timeTextView;
    TextView validTextView;
    LinearLayout parentLayout;

    public VoteViewHolder(View itemView) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.vote_layout);

        publicKeyTextView = itemView.findViewById(R.id.vote_publicKey);
        signatureTextView = itemView.findViewById(R.id.vote_signature);
        timeTextView = itemView.findViewById(R.id.vote_time);
        choiceTextTextView = itemView.findViewById(R.id.vote_choiceText);
        choiceIdTextView = itemView.findViewById(R.id.vote_choiceId);
        validTextView = itemView.findViewById(R.id.vote_valid);
    }
}
