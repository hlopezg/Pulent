package com.example.pulent.ui.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.databinding.ActivityMainBinding;
import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.repository.Status;
import com.example.pulent.ui.detail.SongDetailActivity;
import com.example.pulent.utils.Constants;
import com.example.pulent.utils.EndlessRecyclerViewScrollListener;
import com.example.pulent.utils.Utilities;
import com.example.pulent.viewmodel.MainActivityViewModel;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MainActivityImp{
    private MainActivityViewModel mainActivityViewModel;
    private SongAdapter songAdapter;
    private ActivityMainBinding activityMainBinding;
    private SongRepository songRepository;

    private final String mediaType = "music";
    private final int limit = 20;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        
        return activityMainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SongDAO songDAO = AppDatabase.getDatabase(getActivity()).songDAO();
        SongApi songApi =  ApiClient.getClient().create(SongApi.class);
        songRepository = new SongRepository(songDAO, songApi);

        initRecyclerView(getActivity(), activityMainBinding);

        initViewModels();

        initTextChangeListener(activityMainBinding.editTextSongSearch);
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //getIntent().putExtras(intent);
            if(intent.getExtras() != null) {
                String type = intent.getExtras().getString("type");
                if (type != null && type.equals(Constants.TYPE_NETWORK_CONECTION)) {
                    setNetworkConnectionStatus(intent.getExtras().getBoolean("connection"));
                }
            }
        }
    };

    private void setNetworkConnectionStatus(boolean hasNetworkConnection){
        mainActivityViewModel.setIsNetworkAvailable(hasNetworkConnection);
    }

    //Load ViewModel and observer
    private void initViewModels(){

        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        SongSearchQuery songSearchQuery = new SongSearchQuery("", mediaType, 0, false);
        mainActivityViewModel.setSongSearchQuery(songSearchQuery);
        setNetworkConnectionStatus(Utilities.isNetworkAvailable(getActivity()));

        mainActivityViewModel.getIsNetworkAvailable().observe(getActivity(), hasNetworkConnection -> activityMainBinding.setHasNetworkConnection(hasNetworkConnection));

        mainActivityViewModel.getState().observe(getActivity(), state -> {
            activityMainBinding.setState(state);
            if(state.getStatus() == Status.ERROR){
                Toast.makeText(getActivity(), state.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        mainActivityViewModel.getLastSongSearchResult().observe(getActivity(), songSearchResults -> {
            if(mainActivityViewModel.getLastSongSearchQuery().getValue() != null)
                //if didn't put any text to search or the response cames with no results
                if(songSearchResults.resultCount == 0){
                    //no text in the search bar
                    if(activityMainBinding.editTextSongSearch.getText().toString().length() == 0){
                        activityMainBinding.textViewTitle.setText(getText(R.string.last_searches));
                        AsyncTask.execute(() -> mainActivityViewModel.setLastSongClicked(songRepository.getRecentSongs())
                        );
                    }else{
                        //no results
                        activityMainBinding.textViewTitle.setText(String.format(getString(R.string.there_is_no_results), activityMainBinding.editTextSongSearch.getText().toString()));
                        songAdapter.setSongs(null);
                    }
                }else {
                    activityMainBinding.textViewTitle.setText(getString(R.string.results));
                    if (mainActivityViewModel.getLastSongSearchQuery().getValue().offset == 0) {
                        songAdapter.setSongs(songSearchResults.songs);
                    } else {    // when is getting more data for the paginator
                        songAdapter.addSongs(songSearchResults.songs);
                    }
                }
        });

        mainActivityViewModel.getLastSongsClicked().observe(getActivity(), songs -> songAdapter.setSongs(songs));
    }

    private void initRecyclerView(Context context, ActivityMainBinding activityMainBinding) {
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
                if(mainActivityViewModel.getLastSongSearchResult().getValue() != null && mainActivityViewModel.getLastSongSearchResult().getValue().songs.size() == totalItemsCount) {
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
        songRepository.insert(song);

        Intent intent = new Intent(getActivity(), SongDetailActivity.class);
        intent.putExtra("trackId", song.getTrackId());
        intent.putExtra("SongClass", song);
        startActivity(intent);
    }
}
