package com.btn.pronotes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.Models.Folder;
import com.btn.pronotes.R;
import com.btn.pronotes.databinding.RvItemFolderMainBinding;
import com.btn.pronotes.interfaces.MainFolderClickListener;

import java.util.List;

//Notes List Adapter Class
public class FolderListMainAdapter extends RecyclerView.Adapter<MainFolderViewHolder> {
    Context context;
    List<Folder> list;
    MainFolderClickListener listener;

    public FolderListMainAdapter(Context context, List<Folder> list, MainFolderClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MainFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainFolderViewHolder(RvItemFolderMainBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainFolderViewHolder holder, int position) {

        holder.binding.tvName.setText(list.get(position).getName());
        if (list.get(position).isSelected()) {
            holder.binding.card.setCardBackgroundColor(
                    ContextCompat.getColor(holder.binding.getRoot().getContext(), R.color.color1));
        } else {
            holder.binding.card.setCardBackgroundColor(
                    ContextCompat.getColor(holder.binding.getRoot().getContext(), R.color.grey));
        }

        holder.binding.card.setOnClickListener(v -> {
            listener.onClick(list.get(position));
            unSelectAll();
            list.get(position).setSelected(true);
            notifyDataSetChanged();
        });
    }

    public void unSelectAll() {
        for (Folder folder : list) {
            folder.setSelected(false);
        }
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

class MainFolderViewHolder extends RecyclerView.ViewHolder {

    RvItemFolderMainBinding binding;

    public MainFolderViewHolder(@NonNull RvItemFolderMainBinding binding) {
        super(binding.getRoot());

        this.binding = binding;


    }
}
