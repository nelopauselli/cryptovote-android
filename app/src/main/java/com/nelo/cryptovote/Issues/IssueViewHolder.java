package com.nelo.cryptovote.Issues;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class IssueViewHolder extends RecyclerView.ViewHolder{
    LinearLayout parentLayout;
    TextView nameTextView;
    AppCompatImageButton detailsButton, resultButton;

    public IssueViewHolder(View itemView) {
        super(itemView);

        parentLayout=itemView.findViewById(R.id.issue_layout);
        nameTextView = itemView.findViewById(R.id.issue_name);
        detailsButton = itemView.findViewById(R.id.issue_details);
        resultButton = itemView.findViewById(R.id.issue_result);
    }
}
