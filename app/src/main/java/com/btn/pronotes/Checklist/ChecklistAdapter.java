package com.btn.pronotes.Checklist;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.btn.pronotes.Checklist.ChecklistNotesActivity;
import com.btn.pronotes.Checklist.ChecklistActivity;
import com.btn.pronotes.Checklist.ChecklistAdapter;
import com.btn.pronotes.Checklist.ChecklistItem;
import com.btn.pronotes.Checklist.ChecklistItem;
import com.btn.pronotes.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {

    private List<ChecklistItem> checklistItems;

    public ChecklistAdapter() {
        checklistItems = new ArrayList<>();
    }

    public void setChecklistItems(List<ChecklistItem> items) {
        checklistItems.clear();
        if (items != null) {
            checklistItems.addAll(items);
        }
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
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    class ChecklistViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private MaterialCheckBox cbItem;

        ChecklistViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.checkBox_item);
            cbItem.setOnCheckedChangeListener(this);
        }

        void bindItem(ChecklistItem item) {
            cbItem.setText(item.getText());
            cbItem.setChecked(item.isChecked());
            cbItem.setClickable(true);
            cbItem.setFocusable(false);
            cbItem.setTag(item);

            // Update the original checked state of the item
            ((ChecklistItem) cbItem.getTag()).setChecked(item.isChecked());
            itemView.setOnClickListener(v -> cbItem.setChecked(!cbItem.isChecked()));
            if (item.isChecked()) {
                cbItem.setPaintFlags(cbItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                cbItem.setPaintFlags(cbItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ChecklistItem item = (ChecklistItem) buttonView.getTag();
            item.setChecked(isChecked);
            // allows cbitems to be checked and unchecked
            if (isChecked) {
                cbItem.setPaintFlags(cbItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                cbItem.setPaintFlags(cbItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        }

        private void onItemCheckedChanged(int position, boolean isChecked) {
            ChecklistItem item = checklistItems.get(position);
            item.setChecked(isChecked);
            notifyItemChanged(position);
        }
    }