package com.nelo.cryptovote.Questions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nelo.cryptovote.Choices.ChoiceActivity;
import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.QuestionResults.QuestionResultActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Urns.UrnListActivity;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {

    private Context context;
    private List<Question> items;

    public QuestionAdapter() {
        items = new ArrayList<>();
    }

    public void setEntities(List<Question> questions) {
        Log.d(getClass().getSimpleName(), "Loading " + questions.size() + " questions");
        this.items = questions;
        this.notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(), "Inflating questions");

        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.question_item, parent, false);

        // Return a new holder instance
        QuestionViewHolder viewHolder = new QuestionViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(QuestionViewHolder holder, final int position) {
        if (items == null)
            return;

        Question question = items.get(position);
        Log.d(getClass().getSimpleName(), "Binding question: " + question.name);

        holder.nameTextView.setText(question.name);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = items.get(position);

                if (question.type == 1) {
                    Intent i = new Intent(context, ChoiceActivity.class);
                    i.putExtra("communityId", question.communityId.toString());
                    i.putExtra("questionId", question.id.toString());

                    context.startActivity(i);
                } else if (question.type == 2) {
                    Intent i = new Intent(context, UrnListActivity.class);
                    i.putExtra("communityId", question.communityId.toString());
                    i.putExtra("questionId", question.id.toString());
                    i.putExtra("questionName", question.name);

                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "Question type" + question.type, Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(context, QuestionResultActivity.class);

                Question question = items.get(position);
                i.putExtra("communityId", question.communityId.toString());
                i.putExtra("questionId", question.id.toString());
                i.putExtra("questionName", question.name);

                context.startActivity(i);
            }
        });

        Log.d(getClass().getSimpleName(), "Question bind");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}