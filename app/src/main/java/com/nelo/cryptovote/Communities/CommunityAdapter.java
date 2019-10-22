package com.nelo.cryptovote.Communities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelo.cryptovote.Domain.Community;
import com.nelo.cryptovote.Members.MemberListActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Issues.IssueListActivity;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder> {

    private Context context;
    private List<Community> items;

    public CommunityAdapter(){
        items=new ArrayList<>();
    }

    public void setEntities(List<Community> communities) {
        Log.d(getClass().getSimpleName(), "Loading " + communities.size() + " communities");
        this.items = communities;
        notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(), "Inflating communities");

        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.community_item, parent, false);

        // Return a new holder instance
        CommunityViewHolder viewHolder = new CommunityViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CommunityViewHolder holder, final int position) {
        if (items == null)
            return;

        Community community = items.get(position);
        Log.d(getClass().getSimpleName(), "Binding community: " + community.name);

        holder.nameTextView.setText(community.name);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, IssueListActivity.class);

                Community community = items.get(position);

                String communityId = community.id.toString();
                i.putExtra("communityId", communityId);
                i.putExtra("communityName", community.name);

                context.startActivity(i);
            }
        });

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CommunityDetailActivity.class);

                Community community = items.get(position);

                i.putExtra("communityId", community.id.toString());
                i.putExtra("communityName", community.name);

                context.startActivity(i);
            }
        });

        holder.membersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MemberListActivity.class);

                Community community = items.get(position);

                String communityId = community.id.toString();
                i.putExtra("communityId", communityId);
                i.putExtra("communityName", community.name);

                context.startActivity(i);
            }
        });

        Log.d(getClass().getSimpleName(), "Community bind");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}