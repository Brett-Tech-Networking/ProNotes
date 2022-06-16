package com.btn.pronotes;

import androidx.cardview.widget.CardView;

import com.btn.pronotes.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick (Notes notes, CardView cardView);
}
