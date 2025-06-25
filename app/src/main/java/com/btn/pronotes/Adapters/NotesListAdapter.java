package com.btn.pronotes.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.NotesClickListener;
import com.btn.pronotes.R;
import com.btn.pronotes.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesClickListener listener;
    private int cardBackgroundColor;
    private RoomDB database;

    public NotesListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.database = RoomDB.getInstance(context);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Notes note = list.get(position);
        holder.textView_title.setText(note.getTitle());
        holder.textView_title.setSelected(true); // Sets horizontal scrolling

        String noteContent = note.getNotes().replace("<br>", "\n");
        // Use HtmlCompat to parse HTML tags and render bold text
        holder.textView_notes.setText(HtmlCompat.fromHtml(noteContent, HtmlCompat.FROM_HTML_MODE_LEGACY));

        holder.textView_date.setText(note.getDate());
        holder.textView_date.setSelected(true); // Sets horizontal scrolling

        // Set star icon based on pin state
        if (note.isPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.ic_star);
        } else {
            holder.imageView_pin.setImageResource(R.drawable.ic_star_border);
        }

        // Handle star click to toggle pin state
        holder.imageView_pin.setOnClickListener(v -> {
            boolean newPinState = !note.isPinned();
            note.setPinned(newPinState);
            database.mainDAO().pin(note.getID(), newPinState);
            notifyItemChanged(position);
            String toastMessage = newPinState ? "Note Pinned" : "Note Unpinned!";
            android.widget.Toast.makeText(context, toastMessage, android.widget.Toast.LENGTH_SHORT).show();
        });

        // Existing code for color settings
        int color_code = 0;
        if (new SharedPreferenceHelper(context).isColorChangingTiles()) {
            color_code = getRandomColor();
            holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code, null));
        } else {
            String colorString = new SharedPreferenceHelper(context).getSelectedColor();
            if (!colorString.isEmpty()) {
                color_code = Color.parseColor(colorString);
                holder.notes_container.setCardBackgroundColor(color_code);
            } else {
                holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.color1, null));
            }
        }

        holder.notes_container.setOnClickListener(v -> listener.onClick(list.get(holder.getAdapterPosition())));

        holder.notes_container.setOnLongClickListener(v -> {
            listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notes_container);
            return true;
        });
    }

    private String formatChecklistPreview(String notesContent) {
        StringBuilder formattedContent = new StringBuilder();
        String[] lines = notesContent.split("\n");

        for (String line : lines) {
            if (line.contains("[x]")) {
                formattedContent.append("☑ ").append(line.replace("- [x] ", "")).append("<br>");
            } else {
                formattedContent.append("☐ ").append(line.replace("- [ ] ", "")).append("<br>");
            }
        }

        return formattedContent.toString();
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());
        return colorCode.get(random_color);
    }

    public void setList(List<Notes> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Notes> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        CardView notes_container;
        TextView textView_title, textView_notes, textView_date;
        ImageView imageView_pin;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notes_container = itemView.findViewById(R.id.notes_container);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_notes = itemView.findViewById(R.id.textView_notes);
            textView_date = itemView.findViewById(R.id.textView_date);
            imageView_pin = itemView.findViewById(R.id.imageView_pin);
        }
    }
}