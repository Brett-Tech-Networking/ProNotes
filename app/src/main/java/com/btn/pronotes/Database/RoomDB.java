package com.btn.pronotes.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;

import java.util.concurrent.Executors;

@Database(entities = {Notes.class, Folder.class, Media.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static String DATABASE_NAME = "NoteApp";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    database.mainDAO().insertFolder(new Folder("All"));
                                }
                            });
                        }
                    })
//                    .addMigrations(RoomDB.MIGRATION_1_2)
//                    .addMigrations(RoomDB.MIGRATION_2_3)
                    .addMigrations()
                    .build();
        }
        return database;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes ADD COLUMN locked INTEGER NOT NULL DEFAULT 0");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes ADD COLUMN folderId INTEGER NOT NULL DEFAULT 0");
        }
    };


    public abstract MainDAO mainDAO();
}
