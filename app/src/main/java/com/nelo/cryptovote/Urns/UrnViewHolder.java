package com.nelo.cryptovote.Urns;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelo.cryptovote.R;

public class UrnViewHolder extends RecyclerView.ViewHolder{
    LinearLayout parentLayout;
    TextView nameTextView;
    AppCompatImageButton detailsButton;
    AppCompatImageButton resultButton;

    public UrnViewHolder(View itemView) {
        super(itemView);

        parentLayout=itemView.findViewById(R.id.urn_layout);
        nameTextView = itemView.findViewById(R.id.urn_name);
        detailsButton = itemView.findViewById(R.id.urn_details);
        resultButton = itemView.findViewById(R.id.urn_result);
    }
}
