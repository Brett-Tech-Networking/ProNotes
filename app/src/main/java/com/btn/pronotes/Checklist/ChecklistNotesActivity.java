package com.btn.pronotes.Checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.btn.pronotes.Checklist.ChecklistNotesActivity;
import com.btn.pronotes.Checklist.ChecklistActivity;
import com.btn.pronotes.Checklist.ChecklistAdapter;
import com.btn.pronotes.Checklist.ChecklistItem;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.btn.pronotes.Checklist.ChecklistNotesActivity;
import com.btn.pronotes.Checklist.ChecklistActivity;
import com.btn.pronotes.Checklist.ChecklistAdapter;
import com.btn.pronotes.Checklist.ChecklistItem;

public class ChecklistNotesActivity extends AppCompatActivity {

    private EditText editTextChecklistItem;
    private RecyclerView recyclerViewChecklist;
    private FloatingActionButton fabAddChecklistItem;
    private Button additembutton;
    private ArrayList<ChecklistItem> checklistItems;
    private ChecklistAdapter checklistAdapter;
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_notes);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        editTextChecklistItem = findViewById(R.id.editText_checklist_item);
        recyclerViewChecklist = findViewById(R.id.rv_checklist);
        fabAddChecklistItem = findViewById(R.id.fab_add_checklist_item);
        additembutton = findViewById(R.id.additembutton);
        checklistItems = new ArrayList<>();
       /* checklistItems.add(new ChecklistItem("Option 1", false));*/

        checklistAdapter = new ChecklistAdapter();
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChecklist.setAdapter(checklistAdapter);
        additembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklistItem();
            }
        });
        fabAddChecklistItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addChecklistItem();
            }
        });

        // Get the checklist items passed from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<? extends Parcelable> items = intent.getParcelableArrayListExtra("checklistItems");
            if (items != null) {
                for (Parcelable item : items) {
                    if (item instanceof ChecklistItem) {
                        checklistItems.add((ChecklistItem) item);
                    }
                }
            }
        }
        checklistAdapter.setChecklistItems(checklistItems);
    }

    private void addChecklistItem() {
        String item = editTextChecklistItem.getText().toString().trim();
        if (!item.isEmpty()) {
            ChecklistItem checklistItem = new ChecklistItem(item, false);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    checklistItems.add(checklistItem); // Modify the list after the RecyclerView's layout computation
                    editTextChecklistItem.setText("");
                    checklistAdapter.setChecklistItems(checklistItems); // Update the adapter
                }
            });
        }
    }



    private void saveChecklistAsNote() {
        // Create a new note object
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
        // Save the note to the database or file system here...

        // Finish the activity and return to the previous activity
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isChecklistChanged()) {
            // Save the changes before finishing the activity
            saveChecklistAsNote();
        } else {
            // No changes were made, finish the activity
            super.onBackPressed();
        }
    }

    private boolean isChecklistChanged() {
        for (ChecklistItem item : checklistItems) {
            if (item.isChecked() != item.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Update the checklist items if any changes were made in the ChecklistActivity
            ArrayList<? extends Parcelable> updatedItems = data.getParcelableArrayListExtra("checklistItems");
            if (updatedItems != null) {
                checklistItems.clear();
                for (Parcelable item : updatedItems) {
                    if (item instanceof ChecklistItem) {
                        checklistItems.add((ChecklistItem) item);
                    }
                }
                checklistAdapter.setChecklistItems(checklistItems);
            }
        }
    }
}
