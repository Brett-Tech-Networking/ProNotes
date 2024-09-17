package com.btn.pronotes.Checklist;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {

    private List<ChecklistItem> checklistItems;

    public ChecklistAdapter(ArrayList<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }


    public void setChecklistItems(List<ChecklistItem> items) {
        checklistItems.clear();
        if (items != null) {
            checklistItems.addAll(items);
        }
        Log.d("ChecklistAdapter", "Item count: " + checklistItems.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new ChecklistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder holder, int position) {
        ChecklistItem item = checklistItems.get(position);
        Log.d("ChecklistAdapter", "Binding item at position: " + position + " with text: " + item.getText());
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    class ChecklistViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private MaterialCheckBox cbItem;
        private TextView textItem;

        ChecklistViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.checkbox_item);
            textItem = itemView.findViewById(R.id.text_item);
            cbItem.setOnCheckedChangeListener(this);
        }

        void bindItem(ChecklistItem item) {
            cbItem.setTag(item);  // Set the tag before accessing it later
            textItem.setText(item.getText());
            cbItem.setChecked(item.isChecked());
            cbItem.setClickable(true);
            cbItem.setFocusable(false);
            textItem.setTextColor(Color.WHITE);

            itemView.setOnClickListener(v -> cbItem.setChecked(!cbItem.isChecked()));
            if (item.isChecked()) {
                textItem.setPaintFlags(textItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textItem.setPaintFlags(textItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ChecklistItem item = (ChecklistItem) buttonView.getTag();
            if (item != null) {  // Check if the item is not null before accessing it
                item.setChecked(isChecked);
                // allows cbItems to be checked and unchecked
                if (isChecked) {
                    textItem.setPaintFlags(textItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textItem.setPaintFlags(textItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        }
    }

}