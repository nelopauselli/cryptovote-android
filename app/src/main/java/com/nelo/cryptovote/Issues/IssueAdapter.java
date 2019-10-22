package com.nelo.cryptovote.Issues;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nelo.cryptovote.Choices.ChoiceActivity;
import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.IssueResults.IssueResultActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Urns.UrnListActivity;

import java.util.ArrayList;
import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueViewHolder> {

    private Context context;
    private List<Issue> items;

    public IssueAdapter() {
        items = new ArrayList<>();
    }

    public void setEntities(List<Issue> issues) {
        Log.d(getClass().getSimpleName(), "Loading " + issues.size() + " issues");
        this.items = issues;
        this.notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(), "Inflating issues");

        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.issue_item, parent, false);

        // Return a new holder instance
        IssueViewHolder viewHolder = new IssueViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(IssueViewHolder holder, final int position) {
        if (items == null)
            return;

        Issue issue = items.get(position);
        Log.d(getClass().getSimpleName(), "Binding issue: " + issue.name);

        holder.nameTextView.setText(issue.name);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Issue issue = items.get(position);

                if (issue.type == 1) {
                    Intent i = new Intent(context, ChoiceActivity.class);
                    i.putExtra("communityId", issue.communityId.toString());
                    i.putExtra("issueId", issue.id.toString());

                    context.startActivity(i);
                } else if (issue.type == 2) {
                    Intent i = new Intent(context, UrnListActivity.class);
                    i.putExtra("communityId", issue.communityId.toString());
                    i.putExtra("issueId", issue.id.toString());
                    i.putExtra("issueName", issue.name);

                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "Issue type" + issue.type, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(context, "TODO: Mostrar Detalle", Toast.LENGTH_SHORT).show();
            }
        });

        holder.resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, IssueResultActivity.class);

                Issue issue = items.get(position);
                i.putExtra("communityId", issue.communityId.toString());
                i.putExtra("issueId", issue.id.toString());
                i.putExtra("issueName", issue.name);

                context.startActivity(i);
            }
        });

        Log.d(getClass().getSimpleName(), "Issue bind");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}