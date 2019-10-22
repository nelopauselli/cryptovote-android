package com.nelo.cryptovote.Communities;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class CommunityViewHolder extends RecyclerView.ViewHolder {
    LinearLayout parentLayout;
    TextView nameTextView;
    AppCompatImageButton detailsButton, membersButton;

    public CommunityViewHolder(View itemView) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.community_layout);
        nameTextView = itemView.findViewById(R.id.community_name);
        detailsButton = itemView.findViewById(R.id.community_details);
        membersButton = itemView.findViewById(R.id.community_members);
    }
}