package com.btn.pronotes.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    long insert(Notes notes);
    @Insert(onConflict = REPLACE)
    void insertListOFNotes(List<Notes> notes);

    //List Notes
    @Query("SELECT * FROM notes WHERE folder_id ==:folderID ORDER BY pinned DESC, `index` ASC")
//Show pinned notes on top by descending order
    List<Notes> getAll(int folderID);
    @Query("SELECT * FROM notes  ORDER BY pinned DESC, `index` ASC")
//Show pinned notes on top by descending order
    List<Notes> getAll();

    @Query("SELECT * FROM notes WHERE pinned = true")
    List<Notes> getPinNotes();

    @Query("DELETE  FROM notes ")
    void deleteAllNotes();

    //Delete All Folders
    @Query("DELETE  FROM folder")
    void deleteAllFolders();

    //Update Items
    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID =:id")
    void update(int id, String title, String notes);

    @Query("UPDATE notes SET folder_id = :folderID WHERE ID =:id")
    void updateNoteFolder(int id, int folderID);


    //Delete Notes
    @Delete
    void delete(Notes notes);

    //Update pinned
    @Query("UPDATE notes SET pinned = :pin WHERE ID = :id")
    void pin(int id, boolean pin);

    @Query("UPDATE notes SET locked = :lock WHERE ID = :id")
    void lock(int id, boolean lock);


    // implement code to update re arranged notes

    //update index
    @Query("UPDATE notes SET `index` = :index WHERE ID =:id")
    void updateIndex(int id, int index);
    @Query("SELECT COUNT(*) FROM notes")
    int getNotesCount();
    @Query("SELECT COUNT(*) FROM notes WHERE folder_id = :folderID")
    int getNotesCount(int folderID);

    // folder

    @Insert(onConflict = REPLACE)
    void insertFolder(Folder folder);

    @Query("SELECT * FROM folder")
    List<Folder> getAllFolder();

    @Query("SELECT COUNT(*) FROM folder")
    int getFolderCount();

    @Query("UPDATE folder SET is_selected = :isSelect WHERE ID = :id")
    void selectFolder(int id, boolean isSelect);
    @Query("UPDATE folder SET count = :count WHERE ID = :id")
    void setFolderItemCount(int id, int count);

    @Delete
    void deleteFolder(Folder folder);

    // media
    @Insert(onConflict = REPLACE)
    void insertMedia(List<Media> mediaList);



    @Query("SELECT * FROM media where note_id = :noteID ")
    List<Media> getAllMedia(long noteID);

    @Query("DELETE  FROM media where path = :path ")
    void deleteMedia(String path);
    @Query("DELETE  FROM media")
    void deleteALLMedia();
}
