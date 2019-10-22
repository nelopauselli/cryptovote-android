package com.nelo.cryptovote.Members;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelo.cryptovote.Domain.Member;
import com.nelo.cryptovote.R;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    private Context context;
    private List<Member> items;

    public MemberAdapter(){
        items=new ArrayList<>();
    }

    public void setEntities(List<Member> members) {
        Log.d(getClass().getSimpleName(), "Loading " +members.size() + " members");
        this.items.clear();
        this.items.addAll(members);
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(), "Inflating members");

        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View memberView = inflater.inflate(R.layout.member_item, parent, false);

        // Return a new holder instance
        MemberViewHolder viewHolder = new MemberViewHolder(memberView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MemberViewHolder holder, final int position) {
        if (items == null)
            return;

        Member member = items.get(position);
        Log.d(getClass().getSimpleName(), "Binding member: " + member.name);

        holder.nameTextView.setText(member.name);
        holder.addressTextView.setText(member.address);

        Log.d(getClass().getSimpleName(), "Member bind");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}