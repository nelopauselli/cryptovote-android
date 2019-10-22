package com.nelo.cryptovote.Members;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class MemberViewHolder extends RecyclerView.ViewHolder{
    TextView nameTextView;
    TextView addressTextView;

    public MemberViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.member_name);
        addressTextView = itemView.findViewById(R.id.member_address);
    }
}
