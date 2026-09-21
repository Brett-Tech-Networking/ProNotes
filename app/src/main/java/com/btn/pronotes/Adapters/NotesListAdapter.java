package com.btn.pronotes.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.NotesClickListener;
import com.btn.pronotes.R;
import com.btn.pronotes.utils.NoteSculptor;
import com.btn.pronotes.utils.SharedPreferenceHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesClickListener listener;
    private final RoomDB database;
    private final float corner3dPx;
    private final float cornerFlatPx;
    private final float strokePx;
    private final int extrudeOffsetPx;

    private static final int[] TILE_COLORS = {
            R.color.color1,
            R.color.yellow,
            R.color.color3,
            R.color.color4,
            R.color.color5
    };

    public NotesListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.database = RoomDB.getInstance(context);
        this.corner3dPx = NoteSculptor.dp(context.getResources(), 18f);
        this.cornerFlatPx = NoteSculptor.dp(context.getResources(), 12f);
        this.strokePx = NoteSculptor.dp(context.getResources(), 1.25f);
        this.extrudeOffsetPx = Math.round(NoteSculptor.dp(context.getResources(), 7f));
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
        holder.textView_title.setSelected(true);

        String noteContent = note.getNotes().replace("<br>", "\n");
        holder.textView_notes.setText(HtmlCompat.fromHtml(noteContent, HtmlCompat.FROM_HTML_MODE_LEGACY));

        holder.textView_date.setText(note.getDate());
        holder.textView_date.setSelected(true);

        if (note.isPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.ic_star);
        } else {
            holder.imageView_pin.setImageResource(R.drawable.ic_star_border);
        }

        holder.imageView_pin.setOnClickListener(v -> {
            boolean newPinState = !note.isPinned();
            note.setPinned(newPinState);
            database.mainDAO().pin(note.getID(), newPinState);
            notifyItemChanged(position);
            String toastMessage = newPinState ? "Note Pinned" : "Note Unpinned!";
            android.widget.Toast.makeText(context, toastMessage, android.widget.Toast.LENGTH_SHORT).show();
        });

        boolean use3d = new SharedPreferenceHelper(context).is3DNotesEnabled();
        int baseColor = resolveBaseColor(note);
        if (use3d) {
            applySculptedStyle(holder, baseColor);
        } else {
            applyFlatStyle(holder, baseColor);
        }

        holder.noteRoot.animate().cancel();
        holder.noteRoot.setScaleX(1f);
        holder.noteRoot.setScaleY(1f);
        holder.noteRoot.setTranslationY(0f);

        holder.notes_container.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(list.get(adapterPosition));
            }
        });

        holder.notes_container.setOnTouchListener(new View.OnTouchListener() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private float startX, startY;
            private boolean isMoved = false;
            private boolean isLongPressed = false;

            private final Runnable popupRunnable = () -> {
                if (!isMoved) {
                    isLongPressed = true;
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onLongClick(list.get(adapterPosition), holder.notes_container);
                    }
                }
            };

            private void lift() {
                if (!use3d) {
                    return;
                }
                holder.noteRoot.animate()
                        .translationY(-6f)
                        .scaleX(1.03f)
                        .scaleY(1.03f)
                        .setDuration(110)
                        .start();
            }

            private void rest() {
                if (!use3d) {
                    return;
                }
                holder.noteRoot.animate()
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(140)
                        .start();
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        isMoved = false;
                        isLongPressed = false;
                        lift();
                        handler.postDelayed(popupRunnable, 800);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isMoved && !isLongPressed) {
                            if (Math.abs(event.getX() - startX) > 15 || Math.abs(event.getY() - startY) > 15) {
                                isMoved = true;
                                handler.removeCallbacks(popupRunnable);
                                long duration = event.getEventTime() - event.getDownTime();
                                if (duration > 300) {
                                    listener.onStartDrag(holder);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(popupRunnable);
                        rest();
                        break;
                }
                return false;
            }
        });
    }

    @ColorInt
    private int resolveBaseColor(Notes note) {
        SharedPreferenceHelper prefs = new SharedPreferenceHelper(context);
        if (prefs.isColorChangingTiles()) {
            int colorRes = TILE_COLORS[Math.floorMod(note.getID(), TILE_COLORS.length)];
            return ContextCompat.getColor(context, colorRes);
        }
        String colorString = prefs.getSelectedColor();
        if (!colorString.isEmpty()) {
            return Color.parseColor(colorString);
        }
        return ContextCompat.getColor(context, R.color.color1);
    }

    private void applySculptedStyle(NotesViewHolder holder, @ColorInt int base) {
        holder.noteGlow.setVisibility(View.VISIBLE);
        holder.noteExtrude.setVisibility(View.VISIBLE);
        setCardMargins(holder.notes_container, 0, 0, extrudeOffsetPx - Math.round(NoteSculptor.dp(context.getResources(), 1f)), extrudeOffsetPx);
        setViewMargins(holder.noteExtrude, Math.round(NoteSculptor.dp(context.getResources(), 6f)), extrudeOffsetPx, 0, 0);
        setViewMargins(holder.noteGlow, Math.round(NoteSculptor.dp(context.getResources(), 2f)), Math.round(NoteSculptor.dp(context.getResources(), 6f)), Math.round(NoteSculptor.dp(context.getResources(), 2f)), 0);

        holder.noteGlow.setBackground(NoteSculptor.glow(base, corner3dPx + NoteSculptor.dp(context.getResources(), 2f)));
        holder.noteExtrude.setBackground(NoteSculptor.extrude(base, corner3dPx));
        holder.noteFace.setBackground(NoteSculptor.face(base, corner3dPx, strokePx));
        holder.notes_container.setCardBackgroundColor(Color.TRANSPARENT);
        holder.notes_container.setRadius(corner3dPx);
        holder.notes_container.setStrokeWidth(0);

        applyInkColors(holder);
    }

    private void applyFlatStyle(NotesViewHolder holder, @ColorInt int base) {
        holder.noteGlow.setVisibility(View.GONE);
        holder.noteExtrude.setVisibility(View.GONE);
        setCardMargins(holder.notes_container, 0, 0, 0, 0);
        holder.noteFace.setBackground(null);

        holder.notes_container.setCardBackgroundColor(base);
        holder.notes_container.setRadius(cornerFlatPx);
        holder.notes_container.setStrokeColor(ContextCompat.getColor(context, R.color.note_card_stroke));
        holder.notes_container.setStrokeWidth(Math.round(NoteSculptor.dp(context.getResources(), 1f)));
        holder.notes_container.setCardElevation(0f);

        applyInkColors(holder);
    }

    private void applyInkColors(NotesViewHolder holder) {
        int title = ContextCompat.getColor(context, R.color.note_text_primary);
        int body = ContextCompat.getColor(context, R.color.note_text_body);
        int meta = ContextCompat.getColor(context, R.color.note_text_secondary);
        holder.textView_title.setTextColor(title);
        holder.textView_notes.setTextColor(body);
        holder.textView_date.setTextColor(meta);
        holder.imageView_pin.setColorFilter(meta);
    }

    private void setCardMargins(MaterialCardView card, int start, int top, int end, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        params.setMargins(start, top, end, bottom);
        card.setLayoutParams(params);
    }

    private void setViewMargins(View view, int start, int top, int end, int bottom) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(start, top, end, bottom);
        view.setLayoutParams(params);
    }

    public void setList(List<Notes> list) {
        if (list == null) {
            this.list = new ArrayList<>();
            return;
        }
        // Copy so we never clear()+addAll() on the same list instance (that wipes the data).
        this.list = new ArrayList<>(list);
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
        View noteRoot;
        View noteGlow;
        View noteExtrude;
        View noteFace;
        MaterialCardView notes_container;
        TextView textView_title, textView_notes, textView_date;
        ImageView imageView_pin;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteRoot = itemView.findViewById(R.id.note_root);
            noteGlow = itemView.findViewById(R.id.note_glow);
            noteExtrude = itemView.findViewById(R.id.note_extrude);
            noteFace = itemView.findViewById(R.id.note_face);
            notes_container = itemView.findViewById(R.id.notes_container);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_notes = itemView.findViewById(R.id.textView_notes);
            textView_date = itemView.findViewById(R.id.textView_date);
            imageView_pin = itemView.findViewById(R.id.imageView_pin);
        }
    }
}
