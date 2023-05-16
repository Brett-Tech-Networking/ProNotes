package com.btn.pronotes.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.NotesClickListener;
import com.btn.pronotes.R;
import com.btn.pronotes.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Notes List Adapter Class
public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesClickListener listener;

    public NotesListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_title.setSelected(true); //sets horizontal scrolling
        Spanned spannedString = Html.fromHtml(list.get(position).getNotes());

        holder.textView_notes.setText(spannedString);

        holder.textView_date.setText(list.get(position).getDate());
        holder.textView_date.setSelected(true); //sets horizontal scrolling

        if (list.get(position).isPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.ic_pin);
        } else {
            holder.imageView_pin.setImageResource(0);
        }
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

    // get random color for note tiles
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

    //get static color for note tiles
    private int getStaticColor() {
        List<Integer> colorCode2 = new ArrayList<>();

        colorCode2.add(R.color.color3);

        Random random = new Random();
        int random_color = random.nextInt(colorCode2.size());
        return colorCode2.get(random_color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Notes> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}

//Notes View Holder Class
class NotesViewHolder extends RecyclerView.ViewHolder {
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
