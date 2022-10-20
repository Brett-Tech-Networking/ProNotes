package com.btn.pronotes.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btn.pronotes.Models.Notes;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    //List Notes
    @Query("SELECT * FROM notes ORDER BY pinned DESC")//Show pinned notes on top by descending order
    List<Notes> getAll();

    //Update Items
    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID =:id")
    void update(int id, String title, String notes);

    //Delete Notes
    @Delete
    void delete(Notes notes);

    //Update pinned
    @Query("UPDATE notes SET pinned = :pin WHERE ID = :id")
    void pin(int id, boolean pin);


    // implement code to update re arranged notes
}
