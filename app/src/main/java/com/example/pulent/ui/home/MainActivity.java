package com.example.pulent.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.databinding.ActivityMainBinding;
import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.ui.AppExecutors;
import com.example.pulent.utils.EndlessRecyclerViewScrollListener;
import com.example.pulent.viewmodel.MainActivityViewModel;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel mainActivityViewModel;
    private SongAdapter songAdapter;
    private SongDAO songDAO;

    private final String mediaType = "music";
    private final int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // bind RecyclerView
        RecyclerView recyclerView = activityMainBinding.songsRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, int visibleThreshold) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(mainActivityViewModel.getLastSongSearchResult() != null && mainActivityViewModel.getLastSongSearchResult().getValue().songs.size() == totalItemsCount) {
                    mainActivityViewModel.getMoreSongs(limit);
                }
            }

            @Override
            public void onHeadFirstVisible(int firstVisible) {

            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        songDAO = AppDatabase.getDatabase(this).songDAO();
        Retrofit apiClient = ApiClient.getClient();
        SongApi songApi = apiClient.create(SongApi.class);
        AppExecutors appExecutors = new AppExecutors();

        SongRepository songRepository = new SongRepository(songDAO, songApi, appExecutors);
        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        //songRepository.loadSongss("in utero", "music", 20);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        SongSearchQuery songSearchQuery = new SongSearchQuery("", mediaType, 0, false);
        mainActivityViewModel.setSongSearchQuery(songSearchQuery);

        songAdapter = new SongAdapter(null);
        recyclerView.setAdapter(songAdapter);

        mainActivityViewModel.getLastSongSearchResult().observe(this, songSearchResults -> {
            if(mainActivityViewModel.getLastSongSearchQuery().getValue().offset == 0) {
                songAdapter.setSongs(songSearchResults.songs);
                songDAO.deleteAll();
            } else{
                songAdapter.addSongs(songSearchResults.songs);
            }
            songDAO.insert(songSearchResults.songs);
        });

        activityMainBinding.editTextSongSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                songAdapter.setSongs(songDAO.findByQuery(s.toString()));
                mainActivityViewModel.getFirstSongs(s.toString(), mediaType, 0, limit);
            }
        });
    }
}
