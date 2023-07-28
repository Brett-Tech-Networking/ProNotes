package com.btn.pronotes.Checklist;

import android.content.Intent;
import android.view.View;
import com.btn.pronotes.Checklist.ChecklistNotesActivity;
import com.btn.pronotes.Checklist.ChecklistActivity;
import com.btn.pronotes.Checklist.ChecklistAdapter;
import com.btn.pronotes.Checklist.ChecklistItem;
import androidx.appcompat.app.AppCompatActivity;

import com.btn.pronotes.Checklist.ChecklistNotesActivity;

public class ChecklistActivity extends AppCompatActivity {
    // Existing code

    public void openChecklistNotes(View view) {
        Intent intent = new Intent(this, ChecklistNotesActivity.class);
        startActivity(intent);
    }
}
