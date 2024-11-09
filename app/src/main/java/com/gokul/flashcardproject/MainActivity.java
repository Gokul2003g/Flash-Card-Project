package com.gokul.flashcardproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_FLASHCARD_REQUEST = 1;
    private RecyclerView recyclerView;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList;
    private FirebaseFirestore db;
    private Button viewFlashcardsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = findViewById(R.id.fab);
        viewFlashcardsButton = findViewById(R.id.viewFlashcardsButton);

        flashcardList = new ArrayList<>();
        adapter = new FlashcardAdapter(flashcardList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        fetchFlashcards();

        fab.setOnClickListener(v -> startActivityForResult(new Intent(MainActivity.this, AddFlashcardActivity.class), ADD_FLASHCARD_REQUEST));

        viewFlashcardsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FlashcardViewActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FLASHCARD_REQUEST && resultCode == RESULT_OK) {
            fetchFlashcards(); // Refresh the flashcards list
        }
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
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            } else {
                Toast.makeText(this, "Error fetching flashcards", Toast.LENGTH_SHORT).show();
            }
        });
    }
}