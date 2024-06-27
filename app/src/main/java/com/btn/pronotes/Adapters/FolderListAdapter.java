package com.btn.pronotes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.R;
import com.btn.pronotes.databinding.RvItemFolderBinding;
import com.btn.pronotes.interfaces.FolderClickListener;
import com.btn.pronotes.interfaces.MainFolderClickListener;

import java.util.List;

//Notes List Adapter Class
public class FolderListAdapter extends RecyclerView.Adapter<FolderViewHolder> {
    Context context;
    List<Folder> list;
    FolderClickListener listener;

    public FolderListAdapter(Context context, List<Folder> list, FolderClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(RvItemFolderBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = list.get(position);

        holder.binding.tvName.setText(folder.getName()); // Change here
        int count = folder.getId() != 1 ?
                RoomDB.getInstance(context).mainDAO().getNotesCount(folder.getId()) :
                RoomDB.getInstance(context).mainDAO().getNotesCount();
        holder.binding.tvCount.setText(String.valueOf(count));

        if (folder.isSelected()) {
            holder.binding.ivCurrentIcon.setVisibility(View.VISIBLE);
            holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvCount.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.card.setStrokeWidth(1);
        } else {
            holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.grey1));
            holder.binding.tvCount.setTextColor(ContextCompat.getColor(context, R.color.grey1));
            holder.binding.ivCurrentIcon.setVisibility(View.INVISIBLE);
            holder.binding.card.setStrokeWidth(0);
        }

        holder.binding.card.setOnClickListener(v -> listener.onClick(folder));

        // Handle delete button click
        holder.binding.deletebutton.setOnClickListener(v -> {
            RoomDB.getInstance(context).mainDAO().deleteFolder(folder);
            list.remove(position);
            notifyItemRemoved(position);
        });
    }

    public void setList(List<Folder> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}

class FolderViewHolder extends RecyclerView.ViewHolder {

    RvItemFolderBinding binding;

    public FolderViewHolder(@NonNull RvItemFolderBinding binding) { // Change here
        super(binding.getRoot());
        this.binding = binding;
    }
}
