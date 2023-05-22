package com.btn.pronotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {

    private List<String> checklistItems;

    public ChecklistAdapter() {
        checklistItems = new ArrayList<>();
    }

    public void setChecklistItems(List<String> items) {
        checklistItems.clear();
        if (items != null) {
            checklistItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new ChecklistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder holder, int position) {
        String item = checklistItems.get(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    public class ChecklistViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;

        public ChecklistViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox_item);
        }

        public void bindItem(String item) {
            checkBox.setText(item);
        }
    }
}
