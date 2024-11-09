package com.gokul.flashcardproject;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class FlashcardViewActivity extends AppCompatActivity {

    private TextView flashcardTextView;
    private Button shuffleButton;
    private ToggleButton knownToggleButton;
    private List<Flashcard> flashcardList;
    private int currentIndex = 0;
    private boolean showingQuestion = true;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        flashcardTextView = findViewById(R.id.flashcardTextView);
        shuffleButton = findViewById(R.id.shuffleButton);
        knownToggleButton = findViewById(R.id.knownToggleButton);
        db = FirebaseFirestore.getInstance();

        fetchFlashcards();

        flashcardTextView.setOnClickListener(v -> flipFlashcard());
        shuffleButton.setOnClickListener(v -> shuffleFlashcards());
        knownToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> markAsKnown(isChecked));
    }

    private void fetchFlashcards() {
        db.collection("flashcards").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                flashcardList = task.getResult().toObjects(Flashcard.class);
                if (!flashcardList.isEmpty()) {
                    displayFlashcard();
                }
            } else {
                Toast.makeText(this, "Error fetching flashcards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFlashcard() {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Flashcard flashcard = flashcardList.get(currentIndex);
            flashcardTextView.setText(showingQuestion ? flashcard.getQuestion() : flashcard.getAnswer());
            knownToggleButton.setChecked(flashcard.isKnown());
        }
    }

    private void flipFlashcard() {
        showingQuestion = !showingQuestion;
        displayFlashcard();
    }

    private void shuffleFlashcards() {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Collections.shuffle(flashcardList);
            currentIndex = 0;
            showingQuestion = true;
            displayFlashcard();
        }
    }

    private void markAsKnown(boolean isKnown) {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Flashcard flashcard = flashcardList.get(currentIndex);
            flashcard.setKnown(isKnown);
            db.collection("flashcards").document(flashcard.getId())
                    .update("known", isKnown)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Flashcard updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating flashcard", Toast.LENGTH_SHORT).show());
        }
    }
}