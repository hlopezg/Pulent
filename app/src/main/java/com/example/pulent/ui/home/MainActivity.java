package com.example.pulent.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.databinding.ActivityMainBinding;
import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.ui.detail.SongDetailActivity;
import com.example.pulent.utils.Constants;
import com.example.pulent.utils.EndlessRecyclerViewScrollListener;
import com.example.pulent.utils.Utilities;
import com.example.pulent.viewmodel.MainActivityViewModel;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;

public class MainActivity extends AppCompatActivity implements MainActivityImp{
    private MainActivityViewModel mainActivityViewModel;
    private SongAdapter songAdapter;

    private final String mediaType = "music";
    private final int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("MyData"));

        initRecyclerView(this, activityMainBinding);

        initViewModels();

        initTextChangeListener(activityMainBinding.editTextSongSearch);
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getIntent().putExtras(intent);
            if(intent.getExtras() != null) {
                String type = intent.getExtras().getString("type");
                if (type != null && type.equals(Constants.TYPE_NETWORK_CONECTION)) {
                    mainActivityViewModel.isNetworkAvailable = intent.getExtras().getBoolean("connection");
                }
            }
        }
    };

    private void initViewModels(){
        //Load ViewModel and observer
        SongDAO songDAO = AppDatabase.getDatabase(this).songDAO();
        SongApi songApi =  ApiClient.getClient().create(SongApi.class);

        SongRepository songRepository = new SongRepository(songDAO, songApi);
        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        SongSearchQuery songSearchQuery = new SongSearchQuery("", mediaType, 0, false);
        mainActivityViewModel.setSongSearchQuery(songSearchQuery);
        mainActivityViewModel.isNetworkAvailable = Utilities.isNetworkAvailable(this);

        mainActivityViewModel.getLastSongSearchResult().observe(this, songSearchResults -> {
            //AsyncTask.execute(() -> {
                if (mainActivityViewModel.getLastSongSearchQuery().getValue().offset == 0) {
                    songAdapter.setSongs(songSearchResults.songs);
                    //songDAO.deleteAll();
                } else {
                    songAdapter.addSongs(songSearchResults.songs);
                }
                //songDAO.insert(songSearchResults.songs);
            //});
        });
    }

    private void initRecyclerView(Context context, ActivityMainBinding activityMainBinding){
        // bind RecyclerView
        RecyclerView recyclerView = activityMainBinding.songsRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        songAdapter = new SongAdapter(null, this);
        recyclerView.setAdapter(songAdapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, int visibleThreshold) {
                // Triggered only when new data needs to be appended to the list
                if(mainActivityViewModel.getLastSongSearchResult() != null && mainActivityViewModel.getLastSongSearchResult().getValue().songs.size() == totalItemsCount) {
                    mainActivityViewModel.getMoreSongs(limit);
                }
            }

            @Override
            public void onHeadFirstVisible(int firstVisible) {

            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initTextChangeListener(TextView editTextSongSearch){
        editTextSongSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mainActivityViewModel.getFirstSongs(s.toString(), mediaType, 0, limit);
            }
        });
    }

    @Override
    public void onCardViewClickListener(Song song) {
        Intent intent = new Intent(this, SongDetailActivity.class);
        intent.putExtra("trackId", song.getTrackId());
        startActivity(intent);
    }
}
