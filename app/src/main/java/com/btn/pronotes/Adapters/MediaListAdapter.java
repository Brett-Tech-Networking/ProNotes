package com.btn.pronotes.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btn.pronotes.databinding.RvMediaBinding;
import com.btn.pronotes.interfaces.MediaClickListener;

import java.util.List;

//Notes List Adapter Class
public class MediaListAdapter extends RecyclerView.Adapter<MediaViewHolder> {
    Context context;
    List<String> list;
    MediaClickListener listener;

    public MediaListAdapter(Context context, List<String> list, MediaClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaViewHolder(RvMediaBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {

//        Glide.with(context)
//                .load(new File(list.get(position)))
//                .into(holder.binding.ivMedia);
        holder.binding.ivMedia.setImageURI(Uri.parse(list.get(position)));
        holder.binding.ivClose.setOnClickListener(view -> listener.onClickCancel(position));
        holder.binding.card.setOnClickListener(view -> listener.onClick(list.get(position)));

    }

    public void setList(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    public void removeItem(int position){
        list.remove(position);
        notifyItemChanged(position);

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

}

class MediaViewHolder extends RecyclerView.ViewHolder {

    RvMediaBinding binding;

    public MediaViewHolder(@NonNull RvMediaBinding binding) {
        super(binding.getRoot());

        this.binding = binding;


    }
}
