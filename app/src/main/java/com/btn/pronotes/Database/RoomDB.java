package com.btn.pronotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.btn.pronotes.FolderActivity;
import com.btn.pronotes.MainActivity;
import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.R;

import java.util.concurrent.Executors;

@Database(entities = {Notes.class, Folder.class, Media.class}, version = 3, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static final String DATABASE_NAME = "NoteApp";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addCallback(roomCallback)
                    // Include necessary migrations
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
        }
        return database;
    }

    public abstract MainDAO mainDAO();

    // Callback to insert default data when the database is created
    private static final Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert default folder
                    database.mainDAO().insertFolder(new Folder("All Notes"));
                }
            });
        }
    };

    // Migration from version 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Check if 'locked' column exists before adding it
            boolean columnExists = false;
            Cursor cursor = database.query("PRAGMA table_info(notes)");
            int nameColumnIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(nameColumnIndex);
                if ("locked".equals(columnName)) {
                    columnExists = true;
                    break;
                }
            }
            cursor.close();
            if (!columnExists) {
                database.execSQL("ALTER TABLE notes ADD COLUMN locked INTEGER NOT NULL DEFAULT 0");
            }
        }
    };

    // Migration from version 2 to 3
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Check if 'noteType' column exists before adding it
            boolean columnExists = false;
            Cursor cursor = database.query("PRAGMA table_info(notes)");
            int nameColumnIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(nameColumnIndex);
                if ("noteType".equals(columnName)) {
                    columnExists = true;
                    break;
                }
            }
            cursor.close();
            if (!columnExists) {
                database.execSQL("ALTER TABLE notes ADD COLUMN noteType INTEGER NOT NULL DEFAULT 0");
            }
        }
    };
}
