package com.nelo.cryptovote.IssueResults;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelo.cryptovote.Domain.ChoiceRecount;
import com.nelo.cryptovote.Domain.ChoiceResult;
import com.nelo.cryptovote.Domain.IssueResult;
import com.nelo.cryptovote.R;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class IssueResultAdapter extends RecyclerView.Adapter<IssueResultViewHolder> {
    private Context context;
    private IssueResult result;
    ChoiceResult[] sorted;

    IssueResultAdapter() {
    }

    public void setEntities(IssueResult result) {
        this.result = result;

        sorted = result.choices.toArray(new ChoiceResult[0]);
        Arrays.sort(sorted, new Comparator<ChoiceResult>() {
            @Override
            public int compare(ChoiceResult t0, ChoiceResult t1) {
                return Long.compare(t1.votes, t0.votes);
            }
        });

        this.notifyDataSetChanged();
    }

    @Override
    public IssueResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.issue_result_item, parent, false);

        IssueResultViewHolder viewHolder = new IssueResultViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(IssueResultViewHolder holder, final int position) {
        if (result == null)
            return;

        final ChoiceResult choice = sorted[position];

        Log.d(getClass().getSimpleName(), "Binding " + choice.text);
        Log.d(getClass().getSimpleName(), "Color " + choice.color);

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(choice.color);
        int red = (255 + bb.get(1)) % 255;
        int green = (255 + bb.get(2)) % 255;
        int blue = (255 + bb.get(3)) % 255;

        holder.parentLayout.setBackgroundColor(Color.argb(70, red, green, blue));
        holder.orderTextView.setText(String.valueOf(position + 1));
        holder.choiceTextTextView.setText(choice.text);
        holder.choiceIdTextView.setText(choice.choiceId.toString());
        holder.votesTextView.setText(String.valueOf(choice.votes));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (result == null || result.choices == null) return 0;

        Log.d("IssueResultApiAdapter", "Items: " + result.choices.size());
        return result.choices.size();
    }
}