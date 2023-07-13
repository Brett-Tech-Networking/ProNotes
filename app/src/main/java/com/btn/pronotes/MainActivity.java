package com.btn.pronotes;

import static com.btn.pronotes.utils.CONSTANTS.BACKUP_ACTION;
import static com.btn.pronotes.utils.CONSTANTS.EMAIL;
import static com.btn.pronotes.utils.CONSTANTS.NOTE;
import static com.btn.pronotes.utils.CONSTANTS.RESTORE_ACTION;

import android.app.Activity;
import android.app.KeyguardManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.btn.pronotes.Adapters.FolderListMainAdapter;
import com.btn.pronotes.Adapters.NotesListAdapter;
import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.databinding.CustomDialogAddBackupBinding;
import com.btn.pronotes.services.BackupService;
import com.btn.pronotes.widgets.NoteWidget;
import com.nambimobile.widgets.efab.FabOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//*************************\\
//      ^_^        ^_^       \\
//             <>              \\
//     Brett Tech Networking     \\
//   support@brett-techrepair.com  \\
// https://wwww.brett-techrepair.com \\
// Code like a pro come learn with BTN \\
//***************************************\\

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    public static Notes STATIC_NOTE = null;
    private static final int LOCK_REQUEST_CODE = 109;
    public static final int FOLDER_REQUEST_CODE = 110;
    public static final int CHANGE_FOLDER_REQUEST_CODE = 111;
    RecyclerView recyclerView;
    RecyclerView rvFolder;
    NotesListAdapter notesListAdapter;
    private FolderListMainAdapter folderListMainAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    SearchView searchView_home;
    Notes selectedNote;
    Folder selectedFolder;
    ImageView ivAddFolder;

    private FabOption fabOptionAddNote;
    private FabOption fabOptionPicture;
    private FabOption fabOptionCheckList;
    private FabOption fabOptionDraw;

    private KeyguardManager keyguardManager;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Welcome To Pro Notes by BTN", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        recyclerView = findViewById(R.id.recycler_home);
        rvFolder = findViewById(R.id.recycler_folder);
        searchView_home = findViewById(R.id.searchView_home);
        database = RoomDB.getInstance(this);
        List<Folder> folders = database.mainDAO().getAllFolder();
        if (folders.isEmpty()) {
            // Handle the case where no folders are available
            // For example, you can create a default folder and assign it to selectedFolder
            selectedFolder = new Folder();
            selectedFolder.setId(1);
            selectedFolder.setName("All Notes");
            database.mainDAO().insertFolder(selectedFolder);
        } else {
            selectedFolder = folders.get(0);
        }

        if (selectedFolder != null) {
            if (selectedFolder.getId() != 1) {
                notes = database.mainDAO().getAll(selectedFolder.getId());
            } else {
                notes = database.mainDAO().getAll();
            }
        }

        fabOptionAddNote = findViewById(R.id.fab_add_note);
        fabOptionCheckList = findViewById(R.id.fab_check_list);
        fabOptionDraw = findViewById(R.id.fab_draw);
        fabOptionPicture = findViewById(R.id.fab_picture);
        ivAddFolder = findViewById(R.id.iv_add_folder);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        setupFolderRecyclerView();
        if (selectedFolder.getId() != 1) {
            notes = database.mainDAO().getAll(selectedFolder.getId());
        } else {
            notes = database.mainDAO().getAll();

        }

        updateRecycler(notes);

        btnListener();
        fabOptionCheckList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChecklistNotesActivity.class);
            intent.putExtra("noteType", 2); // Use 2 to represent checklist note type
            startActivityForResult(intent, 101);
        });

        // Implements the drag and drop movement of the notes
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(notes, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                database.mainDAO().updateIndex(notes.get(fromPosition).getID(), notes.get(toPosition).getIndex());
                database.mainDAO().updateIndex(notes.get(toPosition).getID(), notes.get(fromPosition).getIndex());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void setupFolderRecyclerView() {
        List<Folder> folders = database.mainDAO().getAllFolder();
        if (folders.isEmpty()) {
            // Handle the case where no folders are available
            // Display an error message or create a default folder
        } else {
            if (folders.size() > 1) {
                for (Folder folder : folders) {
                    if (folder.isSelected()) {
                        selectedFolder = folder;
                        break;
                    }
                }
            } else {
                database.mainDAO().selectFolder(folders.get(0).getId(), true);
                folders.clear();
                folders = database.mainDAO().getAllFolder();
                selectedFolder = folders.get(0);
            }
            folderListMainAdapter = new FolderListMainAdapter(this, folders, newFolder -> {
                if (selectedFolder != newFolder) {
                    database.mainDAO().selectFolder(selectedFolder.getId(), false);
                    database.mainDAO().selectFolder(newFolder.getId(), true);
                    selectedFolder = newFolder;
                    if (newFolder.getId() != 1) {
                        notes = database.mainDAO().getAll(selectedFolder.getId());
                    } else {
                        notes = database.mainDAO().getAll();
                    }
                    notesListAdapter.setList(notes);
                    notesListAdapter.notifyDataSetChanged();
                }
            });
            rvFolder.setAdapter(folderListMainAdapter);
            folderListMainAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (STATIC_NOTE != null) {
            saveNote(STATIC_NOTE);
            STATIC_NOTE = null;
        }
        notesListAdapter.notifyDataSetChanged();
        folderListMainAdapter.setList(database.mainDAO().getAllFolder());

    }

    private void btnListener() {
        ivAddFolder.setOnClickListener(v -> {
            Intent intent = new Intent(this, FolderActivity.class);
            startActivityForResult(intent, FOLDER_REQUEST_CODE);
        });

        fabOptionAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            startActivityForResult(intent, 101); // adding note 101
        });

        fabOptionCheckList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            startActivityForResult(intent, 101); });

        fabOptionDraw.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DrawingActivity.class);
            intent.putExtra("FROM", "main screen");
            startActivity(intent); // adding note 101
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);

            notesListAdapter.setList(database.mainDAO().getAll());
            notesListAdapter.notifyDataSetChanged();
            folderListMainAdapter.setList(database.mainDAO().getAllFolder());
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Open Help Page
        if (item.getItemId() == R.id.help) {
            Intent intent = new Intent(MainActivity.this, OpenHelp.class);
            startActivity(intent);
        }
        //Hide notes
        if (item.getItemId() == R.id.hide) {
            Toast.makeText(this, "Notes Hidden", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);

        }
        //Unhide Notes
        if (item.getItemId() == R.id.unhide) {
            Toast.makeText(this, "Notes Unhidden", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.VISIBLE);
        }
        //Open settings page
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, OpenSettings.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.backup) {
            showBackUpDialog(true);
        }
        if (item.getItemId() == R.id.restore) {
            showBackUpDialog(false);
        }

        //pinned/checked
        if (item.getItemId() == R.id.pin1) {
            selectedNote.isPinned();
            item.setChecked(true);
        } else item.setChecked(false);

        //Launch website http://www.brett-techrepair.com
        if (item.getItemId() == R.id.websitebutton) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brett-techrepair.com")));
        }

        //Bold Button
        if (item.getItemId() == R.id.boldbutton) {
            try {
                TextView Tv = (TextView) findViewById(R.id.editText_notes);
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                Tv.setTypeface(boldTypeface);
            } catch (Exception ignored) {
            }
        }
        //Reboot application in the case of errors
        if (item.getItemId() == R.id.reboot) {
            finish();
            startActivity(getIntent());
            // this provides animation
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    //note search filter code
    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                saveNote(new_notes);
            }
        }
        //Saving Note
        else if (requestCode == 102) {
            if (data != null) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                List<Media> mediaList = new ArrayList<>();
                for (String path : new_notes.getMediaItems()) {
                    Media media = new Media(new_notes.getID(), path);
                    mediaList.add(media);
                }
                List<Media> oldList = database.mainDAO().getAllMedia(new_notes.getID());
                for (int i = oldList.size() - 1; i >= 0; i--) {
                    Media oldItem = oldList.get(i);
                    for (int j = mediaList.size() - 1; j >= 0; j--) {
                        Media newItem = mediaList.get(j);
                        if (oldItem.getPath().equals(newItem.getPath())) {
                            mediaList.remove(j);
                            break;
                        }
                    }
                }
                database.mainDAO().insertMedia(mediaList);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                updateRecycler(notes);
                Toast.makeText(MainActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                updateNoteWidget();
            }

        } else if (requestCode == LOCK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startModifiedNotes(selectedNote);
            } else {
                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHANGE_FOLDER_REQUEST_CODE) {
            if (selectedFolder.getId() != 1) {
                notes = database.mainDAO().getAll(selectedFolder.getId());
            } else {
                notes = database.mainDAO().getAll();
            }
            notesListAdapter.setList(notes);
            notesListAdapter.notifyDataSetChanged();
        } else if (requestCode == FOLDER_REQUEST_CODE) {
            if (data != null) {
                Folder folder = (Folder) data.getSerializableExtra("folder");

                database.mainDAO().selectFolder(selectedFolder.getId(), false);
                database.mainDAO().selectFolder(folder.getId(), true);
                folderListMainAdapter.setList(database.mainDAO().getAllFolder());
                selectedFolder = folder;
                if (selectedFolder.getId() != 1) {
                    notes = database.mainDAO().getAll(selectedFolder.getId());
                } else {
                    notes = database.mainDAO().getAll();
                }
                notesListAdapter.setList(notes);
                notesListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveNote(Notes new_notes) {
        if (notes.size() > 0) {
            new_notes.setIndex(notes.get(notes.size() - 1).getID() + 1);
        } else {
            new_notes.setIndex(1);
        }
        new_notes.setFolderId(selectedFolder.getId());
        long noteId = database.mainDAO().insert(new_notes);
        if (!new_notes.getMediaItems().isEmpty()) {
            List<Media> mediaList = new ArrayList<>();
            for (String path : new_notes.getMediaItems()) {
                Media media = new Media(noteId, path);
                mediaList.add(media);
            }
            database.mainDAO().insertMedia(mediaList);
        }
        if (selectedFolder.getId() != 1) {
            notes = database.mainDAO().getAll(selectedFolder.getId());
        } else {
            notes = database.mainDAO().getAll();
        }

        updateRecycler(notes);

        database.mainDAO().setFolderItemCount(selectedFolder.getId(), selectedFolder.getCount() + 1);

    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
        notesListAdapter.notifyDataSetChanged();
    }

    //modify notes
    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            if (!notes.isLocked()) {
                startModifiedNotes(notes);
            } else {
                selectedNote = notes;
                startLockScreen();
            }
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            selectedFolder = new Folder();
            showPopup(cardView, notes);
        }
    };


    private void startModifiedNotes(Notes notes) {
        Toast.makeText(MainActivity.this, "Modifying Note", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
        intent.putExtra("old_note", notes);
        startActivityForResult(intent, 102);
    }

    private void startLockScreen() {

        if (keyguardManager.isKeyguardSecure()) {
            Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("Authentication Required", "Please enter your PIN or password to unlock note");
            startActivityForResult(lockIntent, LOCK_REQUEST_CODE);
        }
    }

    public void showPopup(CardView cardView, Notes notes) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
        popupMenu.getMenu().getItem(0).setChecked(notes.isPinned());
        popupMenu.getMenu().getItem(1).setChecked(notes.isLocked());

//        if (selectedFolder.getId() == 1) {
//            popupMenu.getMenu().getItem(2).setVisible(false);
//        }
    }


    //Pinning notes //!!make a way to move pinned to top!!
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin1:

                if (selectedNote.isPinned()) {

                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Note Unpinned!", Toast.LENGTH_SHORT).show();

                } else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Note Pinned", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                overridePendingTransition(0, 0);
                String time = System.currentTimeMillis() + "";
                updateNoteWidget();

                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                updateNoteWidget();
                return true;


            case R.id.move:

                Intent intent = new Intent(this, FolderActivity.class);
                intent.putExtra(NOTE, selectedNote);
                startActivityForResult(intent, CHANGE_FOLDER_REQUEST_CODE);
                return true;

            case R.id.lock:
                if (keyguardManager.isKeyguardSecure()) {
                    if (selectedNote.isLocked()) {
                        database.mainDAO().lock(selectedNote.getID(), false);
                        Toast.makeText(MainActivity.this, "Note Unlocked!", Toast.LENGTH_SHORT).show();
                    } else {
                        database.mainDAO().lock(selectedNote.getID(), true);
                        Toast.makeText(MainActivity.this, "Note Locked", Toast.LENGTH_SHORT).show();
                    }
                    notes.clear();
                    notes.addAll(database.mainDAO().getAll());
                    notesListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "The device does not have a PIN or password set", Toast.LENGTH_SHORT).show();

                }
                return true;
            default:
                return false;
        }
    }


    private void updateNoteWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NoteWidget.class));
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.note_widget_layout);

        Intent intent = new Intent(this, NoteWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }

    private void showBackUpDialog(boolean isbackUp) {
        CustomDialogAddBackupBinding dialogBinding = CustomDialogAddBackupBinding.inflate(getLayoutInflater());

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();
        alertDialog.show();
        dialogBinding.tvHeading.setText(isbackUp ? "Backup" : "Restore");
        if (isbackUp) {
            dialogBinding.tvFooter.setText("Note: All of your existing backup data will be lost on backup");
        } else {
            dialogBinding.tvFooter.setText("Note: All of your local data will lost on restore");
        }

        dialogBinding.btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        dialogBinding.btnDone.setOnClickListener(v -> {
            String email = dialogBinding.etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                Intent serviceIntent = new Intent(this, BackupService.class);
                if (isbackUp) {
                    serviceIntent.setAction(BACKUP_ACTION);
                } else {
                    serviceIntent.setAction(RESTORE_ACTION);
                }
                serviceIntent.putExtra(EMAIL, email);
                startService(serviceIntent);
                alertDialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });
    }}
