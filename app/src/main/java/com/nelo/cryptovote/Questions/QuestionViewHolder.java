package com.nelo.cryptovote.Questions;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder{
    LinearLayout parentLayout;
    TextView nameTextView;
    AppCompatImageButton detailsButton, resultButton;

    public QuestionViewHolder(View itemView) {
        super(itemView);

        parentLayout=itemView.findViewById(R.id.question_layout);
        nameTextView = itemView.findViewById(R.id.question_name);
        detailsButton = itemView.findViewById(R.id.question_details);
        resultButton = itemView.findViewById(R.id.question_result);
    }
}
