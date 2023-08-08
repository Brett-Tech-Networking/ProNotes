package com.btn.pronotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Notes;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Notes> notesList;
    private OnNoteClickListener onNoteClickListener;

    public NoteAdapter(List<Notes> notesList, OnNoteClickListener onNoteClickListener) {
        this.notesList = notesList;
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view, onNoteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notes note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.bodyTextView.setText(note.getNotes());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView bodyTextView;
        OnNoteClickListener onNoteClickListener;

        public ViewHolder(@NonNull View itemView, OnNoteClickListener onNoteClickListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            this.onNoteClickListener = onNoteClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Notes selectedNote = notesList.get(position);
                onNoteClickListener.onNoteClick(selectedNote);
            }
        }
    }

    // Interface to handle item click events
    public interface OnNoteClickListener {
        void onNoteClick(Notes note);
    }
}
