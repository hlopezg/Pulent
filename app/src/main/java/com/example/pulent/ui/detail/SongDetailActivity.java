package com.example.pulent.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.pulent.R;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.databinding.ActivitySongDetailBinding;
import com.example.pulent.models.Song;

import java.util.List;

public class SongDetailActivity extends AppCompatActivity {
    private SongListAdapter songAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activitySon activityMainBinding =
        ActivitySongDetailBinding activitySongDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_song_detail);

        if(getIntent().hasExtra("trackId")) {
            long trackId = getIntent().getExtras().getLong("trackId");
            AsyncTask.execute(() -> {
                Song song = AppDatabase.getDatabase(this).songDAO().findSong(trackId);

                List<Song> songs = AppDatabase.getDatabase(this).songDAO().findSongsFromAlbmun(song.getCollectionId());

                initRecyclerView(this, activitySongDetailBinding, songs);

                activitySongDetailBinding.setSongItem(song);
            });
        }
    }

    private void initRecyclerView(Context context, ActivitySongDetailBinding activitySongDetailBinding, List<Song> songs) {
        // bind RecyclerView
        RecyclerView recyclerView = activitySongDetailBinding.recycerViewSongList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        songAdapter = new SongListAdapter(songs);
        recyclerView.setAdapter(songAdapter);
    }
}
