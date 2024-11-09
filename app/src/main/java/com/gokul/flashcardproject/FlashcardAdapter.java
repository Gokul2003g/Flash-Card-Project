package com.gokul.flashcardproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<Flashcard> flashcardList;

    public FlashcardAdapter(List<Flashcard> flashcardList) {
        this.flashcardList = flashcardList;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);
        holder.questionTextView.setText(flashcard.getQuestion());
        holder.answerTextView.setText(flashcard.getAnswer());
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question);
            answerTextView = itemView.findViewById(R.id.answer);
        }
    }
}