package com.btn.pronotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.btn.pronotes.Adapters.NotesListAdapter;
import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchView_home;
    Notes selectedNote;
    int color_code = getRandomColor();
    ImageView imageView_back1;

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Welcome To Pro Notes by BTN", Toast.LENGTH_SHORT).show();
    }

    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());
        return colorCode.get(random_color);
    }

    private int getStaticColor(){
        List<Integer> colorCode2 = new ArrayList<>();

        colorCode2.add(R.color.color3);

        Random random = new Random();
        int random_color = random.nextInt(colorCode2.size());
        return colorCode2.get(random_color);    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_home);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);
        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();


        updateRecycler(notes);


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101); // adding note 101
            }
        });

        // Implements the drag and drop movement of the notes
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(notes, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // End of drag and drop movement

        //search box home code
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
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
            Intent intent = new Intent (MainActivity.this, OpenHelp.class);
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
        //Launch website http://www.brett-techrepair.com
        if (item.getItemId() == R.id.websitebutton) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.brett-techrepair.com")));
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
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }
    private static MainActivity instance;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) ;
            {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                updateRecycler(notes);

            }
        }
        //Saving Note
        else if (requestCode == 102 | resultCode == Activity.RESULT_OK) {
            assert data != null;
            Notes new_notes = (Notes) data.getSerializableExtra("note");
            database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
            notes.clear();
            notes.addAll(database.mainDAO().getAll());
            notesListAdapter.notifyDataSetChanged();
            updateRecycler(notes);
            Toast.makeText(MainActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    //modify notes
    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Toast.makeText(MainActivity.this, "Modifying Note", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }
    };


    public void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }


    //Pinning notes //!!make a way to move pinned to top!!
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
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
                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}


