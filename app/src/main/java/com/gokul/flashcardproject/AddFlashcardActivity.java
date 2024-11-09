package com.gokul.flashcardproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddFlashcardActivity extends AppCompatActivity {

    private EditText questionEditText;
    private EditText answerEditText;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        saveButton = findViewById(R.id.saveButton);
        db = FirebaseFirestore.getInstance();

        saveButton.setOnClickListener(v -> addFlashcard());
    }

    private void addFlashcard() {
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        if (question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            return;
        }

        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(question);
        flashcard.setAnswer(answer);
        flashcard.setKnown(false); // Set known to false by default

        db.collection("flashcards").add(flashcard)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Flashcard added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding flashcard", Toast.LENGTH_SHORT).show());
    }
}