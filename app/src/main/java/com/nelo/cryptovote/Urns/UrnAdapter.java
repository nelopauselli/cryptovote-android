package com.nelo.cryptovote.Urns;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.Recount;
import com.nelo.cryptovote.Domain.Urn;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Recounts.RecountAddActivity;
import com.nelo.cryptovote.Recounts.RecountDetailActivity;
import com.nelo.cryptovote.WebApiAdapters.RecountApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.util.ArrayList;
import java.util.List;

public class UrnAdapter extends RecyclerView.Adapter<UrnViewHolder> {

    private Context context;
    private List<Urn> items;
    private Issue issue;

    public UrnAdapter(Issue issue) {
        this.issue = issue;
        items = new ArrayList<>();
    }

    public void setEntities(List<Urn> urns) {
        Log.d(getClass().getSimpleName(), "Loading " + urns.size() + " urns");
        this.items = urns;
        this.notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UrnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(), "Inflating urns");

        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.urn_item, parent, false);

        // Return a new holder instance
        UrnViewHolder viewHolder = new UrnViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UrnViewHolder holder, final int position) {
        if (items == null)
            return;

        final Urn urn = items.get(position);
        Log.d(getClass().getSimpleName(), "Binding urn: " + urn.name);

        holder.nameTextView.setText(urn.name);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Urn urn = items.get(position);

                Intent i = new Intent(context, RecountAddActivity.class);
                i.putExtra("communityId", issue.communityId.toString());
                i.putExtra("issueId", urn.issueId.toString());
                i.putExtra("urnId", urn.id.toString());
                i.putExtra("issueName", issue.name);
                i.putExtra("urnName", urn.name);

                context.startActivity(i);
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
                RecountApiAdapter api=new RecountApiAdapter(context);
                api.list(urn.id, new RequestListener<Recount>() {
                    @Override
                    public void onComplete(Recount response) {
                        Intent i = new Intent(context, RecountDetailActivity.class);

                        Urn urn = items.get(position);
                        i.putExtra("issue", issue);
                        i.putExtra("urnName", urn.name);
                        i.putExtra("recount", response);

                        context.startActivity(i);
                    }

                    @Override
                    public void onError(int statusCode) {
                    }
                });


            }
        });

        Log.d(getClass().getSimpleName(), "Urn bind");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}