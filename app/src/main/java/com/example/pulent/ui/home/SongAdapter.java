package com.example.pulent.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pulent.R;
import com.example.pulent.databinding.LayoutSongItemBinding;
import com.example.pulent.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Song> songs;
    private HomeCardViewAdapterImp homeCardViewAdapterImp;

    class ViewHolderData extends RecyclerView.ViewHolder {
        private final LayoutSongItemBinding mBinding;
        ViewHolderData(LayoutSongItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(@NonNull Song song) {
            mBinding.setSongItem(song);
            mBinding.executePendingBindings();
        }
    }

    SongAdapter(List<Song> songs, HomeCardViewAdapterImp homeCardViewAdapterImp){
        this.songs = songs;
        this.homeCardViewAdapterImp = homeCardViewAdapterImp;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSongItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_song_item, parent, false);

        return new SongAdapter.ViewHolderData(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SongAdapter.ViewHolderData viewHolder = (SongAdapter.ViewHolderData)holder;
        viewHolder.bind(songs.get(viewHolder.getAdapterPosition()));
        viewHolder.mBinding.cardView.setOnClickListener(v -> {
            homeCardViewAdapterImp.onCardViewClickListener(songs.get(viewHolder.getAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() {
        if(songs == null)
            return 0;
        return songs.size();
    }

    void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    void addSongs(List<Song> songs) {
        if(this.songs == null)
            this.songs = songs;
        else
            this.songs.addAll(songs);
        notifyDataSetChanged();
    }
}
