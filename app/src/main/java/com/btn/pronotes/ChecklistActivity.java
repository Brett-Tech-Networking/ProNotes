package com.btn.pronotes;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChecklistActivity extends AppCompatActivity {
    // Existing code

    public void openChecklistNotes(View view) {
        Intent intent = new Intent(this, ChecklistNotesActivity.class);
        startActivity(intent);
    }
}
