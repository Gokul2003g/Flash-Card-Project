package com.gokul.flashcardproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddFlashcardActivity extends AppCompatActivity {

    private EditText questionEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        questionEditText = findViewById(R.id.questionEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the new flashcard
                String question = questionEditText.getText().toString();
                // Add the new flashcard to the list (this is just a placeholder, you should save it properly)
                // flashcardList.add(new Flashcard(question));
                finish();
            }
        });
    }
}