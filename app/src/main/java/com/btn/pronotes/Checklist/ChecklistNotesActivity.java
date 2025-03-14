package com.btn.pronotes.Checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChecklistNotesActivity extends AppCompatActivity {

    private EditText editTextChecklistItem;
    private EditText editTextTitle;
    private RecyclerView recyclerViewChecklist;
    private ImageButton btnAddChecklistItem;
    private ArrayList<ChecklistItem> checklistItems;
    private ChecklistAdapter checklistAdapter;
    private ImageView backButton;
    private ImageView saveButton;

    private Notes oldNote;
    private boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_notes);

        // Initialize views
        editTextChecklistItem = findViewById(R.id.editText_checklist_item);
        editTextTitle = findViewById(R.id.editText_title);
        recyclerViewChecklist = findViewById(R.id.rv_checklist);
        btnAddChecklistItem = findViewById(R.id.btn_add_checklist_item);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        // Set up RecyclerView and Adapter
        checklistItems = new ArrayList<>();
        checklistAdapter = new ChecklistAdapter(checklistItems);
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChecklist.setAdapter(checklistAdapter);

        // Set up listeners
        btnAddChecklistItem.setOnClickListener(v -> addChecklistItem());

        backButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> saveChecklistAsNote());

        // Handle editing existing note
        if (getIntent().hasExtra("old_note")) {
            oldNote = (Notes) getIntent().getSerializableExtra("old_note");
            isOldNote = true;
            editTextTitle.setText(oldNote.getTitle());
            parseChecklistItems(oldNote.getNotes());
        }

        // Handle text changes in the checklist item input
        editTextChecklistItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                btnAddChecklistItem.setEnabled(s.toString().trim().length() > 0);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        // If checklist items were passed from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<? extends Parcelable> items = intent.getParcelableArrayListExtra("checklistItems");
            if (items != null) {
                for (Parcelable item : items) {
                    if (item instanceof ChecklistItem) {
                        checklistItems.add((ChecklistItem) item);
                    }
                }
                checklistAdapter.setChecklistItems(checklistItems);
            }
        }
    }

    private void addChecklistItem() {
        String item = editTextChecklistItem.getText().toString().trim();
        if (!item.isEmpty()) {
            ChecklistItem checklistItem = new ChecklistItem(item, false);
            checklistItems.add(checklistItem);
            editTextChecklistItem.setText("");
            checklistAdapter.notifyItemInserted(checklistItems.size() - 1);
        }
    }

    private void parseChecklistItems(String notesContent) {
        checklistItems.clear(); // Clear existing items before parsing new ones
        if (notesContent != null && !notesContent.isEmpty()) {
            String[] lines = notesContent.split("\n");
            for (String line : lines) {
                boolean isChecked = line.contains("[x]");
                String text = line.replace("- [x] ", "").replace("- [ ] ", "");
                checklistItems.add(new ChecklistItem(text, isChecked));
            }
            checklistAdapter.notifyDataSetChanged();
        }
    }

    private void saveChecklistAsNote() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            title = "Checklist Note";
        }
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
        newNote.setNoteType(2); // Assuming 2 represents checklist notes

        // Return the note to MainActivity
        Intent intent = new Intent();
        intent.putExtra("note", newNote);
        setResult(Activity.RESULT_OK, intent);
        finish(); // Close the activity after saving
    }

    @Override
    public void onBackPressed() {
        saveChecklistAsNote();
        super.onBackPressed(); // Call super to handle the back button
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle any additional results if needed
        if (requestCode == 101 && resultCode == RESULT_OK) {
            ArrayList<? extends Parcelable> updatedItems = data.getParcelableArrayListExtra("checklistItems");
            if (updatedItems != null) {
                checklistItems.clear();
                for (Parcelable item : updatedItems) {
                    if (item instanceof ChecklistItem) {
                        checklistItems.add((ChecklistItem) item);
                    }
                }
                checklistAdapter.notifyDataSetChanged();
            }
        }
    }
}
