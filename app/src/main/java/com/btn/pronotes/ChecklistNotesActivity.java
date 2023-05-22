package com.btn.pronotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ChecklistNotesActivity extends AppCompatActivity {

    private EditText editTextChecklistItem;
    private RecyclerView recyclerViewChecklist;
    private FloatingActionButton fabAddChecklistItem;

    private List<String> checklistItems;
    private ChecklistAdapter checklistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checklist_notes);
        editTextChecklistItem = findViewById(R.id.editText_checklist_item);
        recyclerViewChecklist = findViewById(R.id.rv_checklist);
        fabAddChecklistItem = findViewById(R.id.fab_add_checklist_item);
        checklistItems = new ArrayList<>();
        checklistItems.add("Option 1");
        checklistItems.add("Option 2");
        checklistItems.add("Option 3");

        checklistAdapter = new ChecklistAdapter();
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChecklist.setAdapter(checklistAdapter);
        fabAddChecklistItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addChecklistItem();
            }
        });

        // Get the checklist items passed from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            List<String> items = intent.getStringArrayListExtra("checklistItems");
            if (items != null) {
                checklistItems.addAll(items);
            }
        }
        checklistAdapter.setChecklistItems(checklistItems);
    }



    private void addChecklistItem() {
        String item = editTextChecklistItem.getText().toString().trim();
        if (!item.isEmpty()) {
            checklistItems.add(item);
            checklistAdapter.setChecklistItems(checklistItems);
            editTextChecklistItem.setText("");
        }
    }

    private void saveChecklistAsNote() {
        // Create a new note object
        String title = "Checklist Note";
        StringBuilder content = new StringBuilder();
        for (String item : checklistItems) {
            content.append("- ").append(item).append("\n");
        }

        // Finish the activity and return to the previous activity
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveChecklistAsNote();
        super.onBackPressed();
    }

    private void saveNote() {
        int index = 1; // Set the desired index value

        // Create a new Notes object with the index
        Notes note = new Notes();

        // Save the note to your data storage or database
        // Example:
        // NoteDatabase.save(note);

        // Finish the activity and return to the previous activity
        setResult(RESULT_OK);
        finish();
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Update the checklist items if any changes were made in the ChecklistActivity
            List<String> updatedItems = data.getStringArrayListExtra("checklistItems");
            if (updatedItems != null) {
                checklistItems.clear();
                checklistItems.addAll(updatedItems);
                checklistAdapter.setChecklistItems(checklistItems);
            }
        }
    }
}
