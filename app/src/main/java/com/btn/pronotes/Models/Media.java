package com.btn.pronotes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "media")
public class Media implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "note_id")
    long noteId;
    @ColumnInfo(name = "path")
    String path;

    public Media() {
    }

    public Media(long noteId, String path) {
        this.noteId = noteId;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
