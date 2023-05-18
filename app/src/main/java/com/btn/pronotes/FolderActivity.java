package com.btn.pronotes;

import static com.btn.pronotes.MainActivity.CHANGE_FOLDER_REQUEST_CODE;
import static com.btn.pronotes.MainActivity.FOLDER_REQUEST_CODE;
import static com.btn.pronotes.utils.CONSTANTS.NOTE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.PopupMenu;

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