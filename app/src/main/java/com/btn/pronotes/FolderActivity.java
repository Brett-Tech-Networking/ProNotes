package com.btn.pronotes;

import static com.btn.pronotes.MainActivity.CHANGE_FOLDER_REQUEST_CODE;
import static com.btn.pronotes.MainActivity.FOLDER_REQUEST_CODE;
import static com.btn.pronotes.utils.CONSTANTS.NOTE;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.btn.pronotes.Adapters.FolderListAdapter;
import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.databinding.ActivityFolderBinding;
import com.btn.pronotes.databinding.CustomDialogAddFolderBinding;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends AppCompatActivity {

    private ActivityFolderBinding binding;
    private RoomDB database;

    private FolderListAdapter folderListAdapter;

    private boolean isChangeFolderEnabled = false;
    private Notes note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = RoomDB.getInstance(this);

        btnListeners();
        setupFolderRecyclerView();

        if (getIntent().hasExtra(NOTE)) {
            isChangeFolderEnabled = true;
            note = (Notes) getIntent().getSerializableExtra(NOTE);
        }
        // Retrieve the noteType from Intent extras
        int noteType = getIntent().getIntExtra("noteType", 1); // Use 1 as the default note type (e.g., text)

        if (noteType == 2) {
            // Handle checklist note creation
            // You can set appropriate flags or variables to indicate that it's a checklist note
            // For example, you can display a different UI for checklist notes or perform checklist-specific operations
            // You can also store the note type in a member variable for future reference

            // Create a default checklist with items
            List<String> checklistItems = new ArrayList<>();
            checklistItems.add("Item 1");
            checklistItems.add("Item 2");
            checklistItems.add("Item 3");

            // TODO: Add code to populate the checklist UI with the default items
            // You need to update your UI accordingly to display the checklist and its items
        } else {
            // Handle other note types (e.g., text note)
            // You can have different logic or behavior for non-checklist notes here
        }
    }

        private void setupFolderRecyclerView() {

        List<Folder> folders = new ArrayList<>(database.mainDAO().getAllFolder());

        folderListAdapter = new FolderListAdapter(this, folders, folder -> {

            if (!isChangeFolderEnabled) {
                Intent intent = new Intent();
                intent.putExtra("folder", folder);
                setResult(FOLDER_REQUEST_CODE, intent);
                finish();
            } else {
                database.mainDAO().updateNoteFolder(note.getID(), folder.getId());
                setResult(CHANGE_FOLDER_REQUEST_CODE);
                finish();
            }
        });

        binding.rvFolderList.setAdapter(folderListAdapter);
        folderListAdapter.notifyDataSetChanged();

    }

    private void btnListeners() {

        binding.btnAddFolder.setOnClickListener(view -> showAddFolderDialog());
    }

    private void showAddFolderDialog() {
        CustomDialogAddFolderBinding dialogBinding = CustomDialogAddFolderBinding.inflate(getLayoutInflater());

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();
        alertDialog.show();

        dialogBinding.btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        dialogBinding.btnDone.setOnClickListener(v -> {
            String folderName = dialogBinding.etFolderName.getText().toString();
            database.mainDAO()
                    .insertFolder(new Folder(folderName.isEmpty() ? "Untitled Name" : folderName));

            folderListAdapter.setList(database.mainDAO().getAllFolder());
            alertDialog.dismiss();
        });
    }
}