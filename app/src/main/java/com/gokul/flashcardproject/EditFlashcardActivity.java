package com.gokul.flashcardproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditFlashcardActivity extends AppCompatActivity {

    private EditText questionEditText;
    private EditText answerEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private String flashcardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        saveButton = findViewById(R.id.saveButton);
        db = FirebaseFirestore.getInstance();

        flashcardId = getIntent().getStringExtra("flashcardId");

        db.collection("flashcards").document(flashcardId).get().addOnSuccessListener(documentSnapshot -> {
            Flashcard flashcard = documentSnapshot.toObject(Flashcard.class);
            if (flashcard != null) {
                questionEditText.setText(flashcard.getQuestion());
                answerEditText.setText(flashcard.getAnswer());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFlashcard();
            }
        });
    }

    private void updateFlashcard() {
        String question = questionEditText.getText().toString();
        String answer = answerEditText.getText().toString();

        if (question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> flashcard = new HashMap<>();
        flashcard.put("question", question);
        flashcard.put("answer", answer);

        db.collection("flashcards").document(flashcardId)
                .set(flashcard)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Flashcard updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating flashcard", Toast.LENGTH_SHORT).show();
                });
    }
}