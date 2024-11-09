package com.gokul.flashcardproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private CardView flashcardCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        flashcardTextView = findViewById(R.id.flashcardTextView);
        shuffleButton = findViewById(R.id.shuffleButton);
        knownToggleButton = findViewById(R.id.knownToggleButton);
        flashcardCardView = findViewById(R.id.flashcardCardView);
        db = FirebaseFirestore.getInstance();

        flashcardList = new ArrayList<>(); // Initialize flashcardList

        fetchFlashcards();

        flashcardCardView.setOnClickListener(v -> flipFlashcard());
        shuffleButton.setOnClickListener(v -> shuffleFlashcards());
        knownToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> markAsKnown(isChecked));
    }

    private void fetchFlashcards() {
        db.collection("flashcards").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                flashcardList.clear(); // Clear the list before adding new items
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Flashcard flashcard = document.toObject(Flashcard.class);
                    if (flashcard != null) {
                        flashcard.setId(document.getId());
                        if (!document.contains("known")) {
                            flashcard.setKnown(false); // Default value if 'known' field is missing
                        }
                        flashcardList.add(flashcard);
                    }
                }
                displayFlashcard(); // Display the first flashcard
            } else {
                Toast.makeText(this, "Error fetching flashcards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFlashcard() {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Flashcard flashcard = flashcardList.get(currentIndex);
            if (showingQuestion) {
                flashcardTextView.setText(flashcard.getQuestion());
                flashcardTextView.setTextColor(getResources().getColor(android.R.color.white)); // Set question text color to black
            } else {
                flashcardTextView.setText(flashcard.getAnswer());
                flashcardTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // Set answer text color to green
            }
            knownToggleButton.setChecked(flashcard.isKnown());
        }
    }

    private void flipFlashcard() {
        flashcardCardView.animate()
                .rotationY(90)
                .setDuration(150)
                .withEndAction(() -> {
                    showingQuestion = !showingQuestion;
                    displayFlashcard();
                    flashcardCardView.setRotationY(-90);
                    flashcardCardView.animate()
                            .rotationY(0)
                            .setDuration(150)
                            .start();
                })
                .start();
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