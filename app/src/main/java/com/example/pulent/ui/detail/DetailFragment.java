package com.example.pulent.ui.detail;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.databinding.FragmentDetailBinding;
import com.example.pulent.models.Song;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;
import com.example.pulent.viewmodel.SongDetailActivityViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements MediaPlayerImp{
    private MediaPlayer mediaPlayer;
    private FragmentDetailBinding binding;
    private FragmentActivity fragmentActivity;
    private Song song;
    private int length = 0;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            song = DetailFragmentArgs.fromBundle(getArguments()).getSong();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(song.getArtistName());

        mediaPlayer = new MediaPlayer();

        SongDAO songDAO = AppDatabase.getDatabase(fragmentActivity).songDAO();
        SongApi songApi =  ApiClient.getClient().create(SongApi.class);
        SongRepository songRepository = new SongRepository(songDAO, songApi);

        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        SongDetailActivityViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(SongDetailActivityViewModel.class);

        viewModel.setSongSelected(song);
        String entity = "song";
        viewModel.getSongs(song.getCollectionId(), entity);

        viewModel.getSongList().observe(fragmentActivity, songs -> initRecyclerView(fragmentActivity, songs));

        viewModel.getSongSelected().observe(fragmentActivity, song -> {
            binding.setSongItem(song);
            binding.setMediaPlayer(this);

            playAudio(song.getPreviewUrl());
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        stopAudio();
    }

    private void initRecyclerView(Context context, List<Song> songs) {
        // bind RecyclerView
        RecyclerView recyclerView = binding.recycerViewSongList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        SongListAdapter songAdapter = new SongListAdapter(songs, this);
        recyclerView.setAdapter(songAdapter);
    }

    @Override
    public void playNewSong(Song song) {
        if(binding.getSongItem().getTrackId() == song.getTrackId()){
            clickOnMedia();
        }else{
            playAudio(song.getPreviewUrl());
            binding.setSongItem(song);
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
        binding.imageViewReproduce.setImageDrawable(getPlayDrawable());
        //runOnUiThread(() -> activitySongDetailBinding.imageViewReproduce.setImageDrawable(getPlayDrawable()));
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

        binding.imageViewReproduce.setImageDrawable(getPlayDrawable());
    }

    @Override
    public void stopAudio() {
        if(isPlaying()) {
            length = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
        binding.imageViewReproduce.setImageDrawable(getPauseDrawable());
    }

    @Override
    public boolean isPlaying() {
        binding.imageViewReproduce.setImageDrawable(getPlayDrawable());
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            this.fragmentActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        fragmentActivity = null;
        super.onDetach();
    }
}
