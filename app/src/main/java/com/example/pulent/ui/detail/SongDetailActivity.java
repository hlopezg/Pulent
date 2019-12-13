package com.example.pulent.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.pulent.R;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.databinding.ActivitySongDetailBinding;
import com.example.pulent.models.Song;

import java.io.IOException;
import java.util.List;

public class SongDetailActivity extends AppCompatActivity implements MediaPlayerImp {
    private MediaPlayer mediaPlayer;
    private ActivitySongDetailBinding activitySongDetailBinding;
    private int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySongDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_song_detail);

        mediaPlayer = new MediaPlayer();

        if(getIntent().hasExtra("trackId")) {
            long trackId = getIntent().getExtras().getLong("trackId");
            AsyncTask.execute(() -> {
                Song song = AppDatabase.getDatabase(this).songDAO().findSong(trackId);

                List<Song> songs = AppDatabase.getDatabase(this).songDAO().findSongsFromAlbmun(song.getCollectionId());

                initRecyclerView(this, activitySongDetailBinding, songs);

                activitySongDetailBinding.setSongItem(song);
                activitySongDetailBinding.setMediaPlayer(this);

                playAudio(song.getPreviewUrl());
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopAudio();
    }

    private void initRecyclerView(Context context, ActivitySongDetailBinding activitySongDetailBinding, List<Song> songs) {
        // bind RecyclerView
        RecyclerView recyclerView = activitySongDetailBinding.recycerViewSongList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        SongListAdapter songAdapter = new SongListAdapter(songs, this);
        recyclerView.setAdapter(songAdapter);
    }

    @Override
    public void playNewSong(Song song) {
        if(activitySongDetailBinding.getSongItem().getTrackId() == song.getTrackId()){
            clickOnMedia();
        }else{
            playAudio(song.getPreviewUrl());
            activitySongDetailBinding.setSongItem(song);
        }
    }

    @Override
    public void playAudio(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> activitySongDetailBinding.imageViewReproduce.setImageDrawable(getPlayDrawable()));

    }

    @Override
    public void clickOnMedia() {
        if (isPlaying()) {
            stopAudio();
        } else {
            resume();
        }
    }

    @Override
    public void resume() {
        /*mediaPlayer.seekTo(length);
        mediaPlayer.setOnSeekCompleteListener(mp -> mediaPlayer.start());*/
        mediaPlayer.start();
        mediaPlayer.seekTo(length);

        activitySongDetailBinding.imageViewReproduce.setImageDrawable(getPlayDrawable());
    }

    @Override
    public void stopAudio() {
        if(isPlaying()) {
            length = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
        activitySongDetailBinding.imageViewReproduce.setImageDrawable(getPauseDrawable());
    }

    @Override
    public boolean isPlaying() {
        activitySongDetailBinding.imageViewReproduce.setImageDrawable(getPlayDrawable());
        return mediaPlayer.isPlaying();
    }

    @Override
    public Drawable getPlayDrawable() {
        return getResources().getDrawable(android.R.drawable.ic_media_play);
    }

    @Override
    public Drawable getPauseDrawable() {
        return getResources().getDrawable(android.R.drawable.ic_media_pause);
    }
}
