package com.example.pulent.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pulent.R;
import com.example.pulent.databinding.LayoutSongListItemBinding;
import com.example.pulent.models.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Song> songs;
    private MediaPlayerImp mediaPlayerImp;

    class ViewHolderData extends RecyclerView.ViewHolder {
        private final LayoutSongListItemBinding mBinding;
        ViewHolderData(LayoutSongListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(@NonNull Song song) {
            mBinding.setSongItem(song);
            mBinding.executePendingBindings();
        }
    }

    SongListAdapter(List<Song> songs, MediaPlayerImp mediaPlayerImp){
        this.songs = songs;
        this.mediaPlayerImp = mediaPlayerImp;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSongListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_song_list_item, parent, false);

        return new SongListAdapter.ViewHolderData(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SongListAdapter.ViewHolderData viewHolder = (SongListAdapter.ViewHolderData)holder;
        viewHolder.bind(songs.get(viewHolder.getAdapterPosition()));
        viewHolder.mBinding.layoutSongListItem.setOnClickListener(v -> {
            if(mediaPlayerImp != null)
                mediaPlayerImp.playNewSong(songs.get(viewHolder.getAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() {
        if(songs == null)
            return 0;
        return songs.size();
    }
}
