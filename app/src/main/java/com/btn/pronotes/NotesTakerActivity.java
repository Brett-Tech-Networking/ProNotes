package com.btn.pronotes;

import static com.btn.pronotes.MainActivity.STATIC_NOTE;
import static com.btn.pronotes.utils.CONSTANTS.FILE_PATH;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Adapters.MediaListAdapter;
import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.interfaces.MediaClickListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.FabOption;
import com.permissionx.guolindev.PermissionX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class NotesTakerActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_DRAW = 113;
    private static final int PICK_IMAGE_REQUEST = 114;
    EditText editText_title;
    RichEditor editText_notes;
    ImageView imageView_save;
    ImageView imageView_back;
    ImageView backButton;
    Notes notes;
    private List<String> mediaList = new ArrayList<>();

    boolean isOldNote = false;

    private AppCompatToggleButton boldBtn;
    private AppCompatToggleButton italicBtn;
    private AppCompatToggleButton underlineBtn;

    private FabOption fabOptionPicture;
    private FabOption fabOptionDrawing;
    private ExpandableFab mainFab;
    private RecyclerView rvMedia;

    private MediaListAdapter mediaListAdapter;

    private RoomDB database;
    TextView textView_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        imageView_save = findViewById(R.id.imageView_save);
        imageView_back = findViewById(R.id.imageView_back);
        backButton = findViewById(R.id.backButton);
        editText_title = findViewById(R.id.editText_title);
        boldBtn = findViewById(R.id.boldbutton);
        italicBtn = findViewById(R.id.italicbutton);
        underlineBtn = findViewById(R.id.underlinebutton);
        fabOptionPicture = findViewById(R.id.fab_picture);
        fabOptionDrawing = findViewById(R.id.fab_draw);
        mainFab = findViewById(R.id.expandable_fab);
        rvMedia = findViewById(R.id.rv_media);
        editText_notes = (RichEditor) findViewById(R.id.editText_notes);
        editText_notes.setPlaceholder("Add Notes:");
        editText_notes.setFontSize(22);
        editText_notes.setTextColor(Color.WHITE);
        editText_notes.setBackgroundColor(Color.BLACK);
        editText_notes.setEditorFontColor(Color.WHITE);
        editText_notes.setPadding(0, 5, 10, 10);

        database = RoomDB.getInstance(this);
        bottomSheetSetup();
        btnListeners();
        setupMediaRecyclerView();
        getArguments();

        setupAutoSaveListeners();
    }

    private void setupAutoSaveListeners() {
        editText_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liveSaveNote();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editText_notes.setOnTextChangeListener(text -> liveSaveNote());
    }

    private void liveSaveNote() {
        String title = editText_title.getText().toString();
        String description = editText_notes.getHtml();

        if (description.isEmpty() && title.isEmpty()) {
            return; // Skip saving if both title and content are empty
        }

        if (notes == null) {
            notes = new Notes();
        }
        notes.setTitle(title);
        notes.setNotes(description);
        notes.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm a").format(new Date()));
        notes.setMediaItems(mediaList);

        if (isOldNote) {
            database.mainDAO().update(notes.getID(), notes.getTitle(), notes.getNotes());
        } else {
            int noteID = (int) database.mainDAO().insert(notes);
            notes.setID(noteID); // Set the ID after inserting
            isOldNote = true;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("note", notes); // Pass the note object
        setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onBackPressed() {
        liveSaveNote(); // Save note before exiting
        super.onBackPressed();
    }

    private void setupMediaRecyclerView() {
        mediaListAdapter = new MediaListAdapter(this, new ArrayList<>(), new MediaClickListener() {
            @Override
            public void onClickCancel(int position) {
                if (deleteFile(position)) {
                    mediaList.remove(position);
                    mediaListAdapter.removeItem(position);
                }
            }

            @Override
            public void onClick(String path) {
                Intent intent = new Intent(NotesTakerActivity.this, ImageActivity.class);
                intent.putExtra(FILE_PATH, path);
                startActivity(intent);
            }
        });
        rvMedia.setAdapter(mediaListAdapter);
    }

    private boolean deleteFile(int position) {
        database.mainDAO().deleteMedia(mediaList.get(position));
        File file = new File(mediaList.get(position));
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "Image not deleted", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getArguments() {
        if (notes == null) {
            notes = new Notes();
        }
        if (getIntent().hasExtra("old_note")) {
            try {
                notes = (Notes) getIntent().getSerializableExtra("old_note");
                editText_title.setText(notes.getTitle());
                editText_notes.setHtml(notes.getNotes());
                isOldNote = true;

                List<Media> list = database.mainDAO().getAllMedia(notes.getID());
                if (list != null) {
                    for (Media media : list) {
                        mediaList.add(media.getPath());
                    }
                    mediaListAdapter.setList(mediaList);
                    mediaListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (getIntent().hasExtra(FILE_PATH)) {
            mediaList.add(getIntent().getStringExtra(FILE_PATH));
            mediaListAdapter.setList(mediaList);
            mediaListAdapter.notifyDataSetChanged();
        }
    }

    private void bottomSheetSetup() {
        FrameLayout bottomSheet = findViewById(R.id.layout_Miscellaneous2);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mainFab.setVisibility(View.VISIBLE);
                } else {
                    mainFab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
    }

    private void btnListeners() {
        imageView_save.setOnClickListener(v -> {
            try {
                liveSaveNote();
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and return to previous UI
            } catch (Exception e) {
                Log.e("NotesTakerActivity", "Error in saveTheNote", e);
                Toast.makeText(this, "Error saving note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        imageView_back.setOnClickListener(view -> finish());

        boldBtn.setOnCheckedChangeListener((compoundButton, b) -> editText_notes.setBold());
        underlineBtn.setOnCheckedChangeListener((compoundButton, b) -> editText_notes.setUnderline());
        italicBtn.setOnCheckedChangeListener((compoundButton, b) -> editText_notes.setItalic());

        fabOptionPicture.setOnClickListener(view -> {
            PermissionX.init(this)
                    .permissions(getPermissionList())
                    .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel"))
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                        } else {
                            Toast.makeText(this, "Permission needed to access pictures", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        fabOptionDrawing.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(getPermissionList())
                    .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel"))
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            Intent intent = new Intent(this, DrawingActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_DRAW);
                        } else {
                            Toast.makeText(this, "Permission needed to draw", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private List<String> getPermissionList() {
        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return permissionList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DRAW) {
            if (data != null && data.hasExtra(FILE_PATH)) {
                mediaList.add(data.getStringExtra(FILE_PATH));
                mediaListAdapter.setList(mediaList);
                mediaListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = copyFileToInternalStorage(uri);
            if (path != null) {
                mediaList.add(path);
                mediaListAdapter.setList(mediaList);
                mediaListAdapter.notifyDataSetChanged();
            }
        }
    }

    private String copyFileToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getExternalFilesDir(null), "IMG" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
