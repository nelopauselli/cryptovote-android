package com.nelo.cryptovote.Votes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.Domain.Vote;
import com.nelo.cryptovote.R;

import java.util.Date;
import java.util.List;

public class VotesAdapter extends RecyclerView.Adapter<VoteViewHolder> {

    private Context context;
    private List<Vote> votes;
    private Question question;

    public void setEntities(List<Vote> votes) {
        this.votes = votes;
        notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public VoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.vote_item, parent, false);

        // Return a new holder instance
        VoteViewHolder viewHolder = new VoteViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(VoteViewHolder holder, final int position) {
        if (votes == null)
            return;

        Vote vote = votes.get(position);
        Log.d(getClass().getSimpleName(), "Procesando voto con publicKey: " + vote.publicKey);
        Log.d(getClass().getSimpleName(), "\t\t signature: " + vote.signature);
        Log.d(getClass().getSimpleName(), "\t\t data: " + Base58.encode(vote.getData()));

        holder.publicKeyTextView.setText(vote.publicKey);
        holder.signatureTextView.setText(vote.signature);

        holder.timeTextView.setText(new Date(vote.time).toString());

        holder.choiceIdTextView.setText(String.valueOf(vote.choiceId));

        for (QuestionChoice choice : question.choices) {
            if (choice.id.equals(vote.choiceId)) {
                holder.parentLayout.setBackgroundColor(choice.color);
                holder.choiceTextTextView.setText(String.valueOf(choice.text));
                break;
            }
        }

        holder.validTextView.setText(vote.isValid() ? "VALID :D" : "INVALID :(");

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, VoteDetailActivity.class);

                Vote vote = votes.get(position);
                i.putExtra("question", question);
                i.putExtra("vote", vote);

                context.startActivity(i);
            }
        });


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return votes == null ? 0 : votes.size();
    }

    public void setQuestion(Question question) {

        this.question = question;
    }
}