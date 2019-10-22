package com.nelo.cryptovote.Choices;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.R;

import java.nio.ByteBuffer;
import java.util.List;

public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceViewHolder> {
    private Context context;
    private List<QuestionChoice> choices;
    private ChoiceListener listener;

    ChoiceAdapter(ChoiceListener listener) {
        this.listener = listener;
    }

    public void setEntities(List<QuestionChoice> choices) {
        this.choices = choices;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.choice_item, parent, false);

        ChoiceViewHolder viewHolder = new ChoiceViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ChoiceViewHolder holder, final int position) {
        if (choices == null)
            return;

        final QuestionChoice choice = choices.get(position);

        Log.d(getClass().getSimpleName(), "Binding " + choice.text);
        Log.d(getClass().getSimpleName(), "Color " + choice.color);

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(choice.color);
        int red = (255 + bb.get(1)) % 255;
        int green = (255 + bb.get(2)) % 255;
        int blue = (255 + bb.get(3)) % 255;

        holder.parentLayout.setBackgroundColor(Color.argb(70, red, green, blue));
        holder.choiceNameTextView.setText(choice.text );
        holder.choiceIdTextView.setText(choice.id.toString());

        holder.voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVote(choice);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return choices == null ? 0 : choices.size();
    }
}