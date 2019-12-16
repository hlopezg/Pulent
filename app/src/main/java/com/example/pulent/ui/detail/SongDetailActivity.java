package com.example.pulent.ui.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.databinding.ActivitySongDetailBinding;
import com.example.pulent.models.Song;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;
import com.example.pulent.viewmodel.SongDetailActivityViewModel;

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

        setSupportActionBar(activitySongDetailBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mediaPlayer = new MediaPlayer();

        SongDAO songDAO = AppDatabase.getDatabase(this).songDAO();
        SongApi songApi =  ApiClient.getClient().create(SongApi.class);
        SongRepository songRepository = new SongRepository(songDAO, songApi);

        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        SongDetailActivityViewModel  mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(SongDetailActivityViewModel.class);

        mainActivityViewModel.getSongList().observe(this, songs -> {
            initRecyclerView(this, activitySongDetailBinding, songs);
        });

        mainActivityViewModel.getSongSelected().observe(this, song -> {
            activitySongDetailBinding.setSongItem(song);
            activitySongDetailBinding.setMediaPlayer(this);

            playAudio(song.getPreviewUrl());
        });

        if(getIntent().hasExtra("trackId")) {
            long trackId = getIntent().getExtras().getLong("trackId");
            AsyncTask.execute(() -> {
                Song song = AppDatabase.getDatabase(this).songDAO().findSong(trackId);
                getSupportActionBar().setTitle(song.getArtistName());

                List<Song> songs = AppDatabase.getDatabase(this).songDAO().findSongsFromAlbmun(song.getCollectionId());

                mainActivityViewModel.setSongSelected(song);
                mainActivityViewModel.setSongList(songs);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
