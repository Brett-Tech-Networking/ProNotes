package com.btn.pronotes.Checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChecklistNotesActivity extends AppCompatActivity {

    private EditText editTextChecklistItem;
    private RecyclerView recyclerViewChecklist;
    private FloatingActionButton fabAddChecklistItem;
    private ArrayList<ChecklistItem> checklistItems;
    private ChecklistAdapter checklistAdapter;
    private ImageView backButton;
    private ImageView saveButton;

    private Notes oldNote;
    private boolean isOldNote = false;
    private boolean isNoteSaved = false; // Flag to prevent multiple saves

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_notes);

        // Initialize views
        editTextChecklistItem = findViewById(R.id.editText_checklist_item);
        recyclerViewChecklist = findViewById(R.id.rv_checklist);
        fabAddChecklistItem = findViewById(R.id.fab_add_checklist_item);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        // Set up RecyclerView and Adapter
        checklistItems = new ArrayList<>();
        checklistAdapter = new ChecklistAdapter(checklistItems);
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChecklist.setAdapter(checklistAdapter);

        // Set up listeners
        fabAddChecklistItem.setOnClickListener(v -> addChecklistItem());
        backButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> saveChecklistAsNote());

        // Handle editing existing note
        if (getIntent().hasExtra("old_note")) {
            oldNote = (Notes) getIntent().getSerializableExtra("old_note");
            isOldNote = true;
            parseChecklistItems(oldNote.getNotes());
        }

        // Enable/disable add button based on input
        editTextChecklistItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                fabAddChecklistItem.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    private void addChecklistItem() {
        String itemText = editTextChecklistItem.getText().toString().trim();
        if (!itemText.isEmpty()) {
            ChecklistItem checklistItem = new ChecklistItem(itemText, false);
            checklistItems.add(checklistItem);
            checklistAdapter.notifyDataSetChanged();
            editTextChecklistItem.setText("");
        } else {
            Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseChecklistItems(String notesContent) {
        String[] lines = notesContent.split("\n");
        for (String line : lines) {
            boolean isChecked = line.contains("[x]");
            String text = line.replace("- [x] ", "").replace("- [ ] ", "");
            checklistItems.add(new ChecklistItem(text, isChecked));
        }
        checklistAdapter.notifyDataSetChanged();
    }

    private void saveChecklistAsNote() {
        if (isNoteSaved) return; // Prevent multiple saves
        isNoteSaved = true;

        if (checklistItems.isEmpty()) {
            Toast.makeText(this, "Checklist is empty", Toast.LENGTH_SHORT).show();
            isNoteSaved = false;
            return;
        }

        String title = "Checklist Note";

        StringBuilder content = new StringBuilder();
        for (ChecklistItem item : checklistItems) {
            content.append("- ");
            if (item.isChecked()) {
                content.append("[x] ");
            } else {
                content.append("[ ] ");
            }
            content.append(item.getText()).append("\n");
        }

        Notes newNote = isOldNote ? oldNote : new Notes();
        newNote.setTitle(title);
        newNote.setNotes(content.toString());
        newNote.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm a").format(System.currentTimeMillis()));
        newNote.setNoteType(2); // Set noteType to 2 for checklists

        // Return the note to MainActivity
        Intent intent = new Intent();
        intent.putExtra("note", newNote);
        setResult(Activity.RESULT_OK, intent);
        finish(); // Close the activity
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveChecklistAsNote();
        // No need to call super.onBackPressed()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle any additional results if needed
    }
}
