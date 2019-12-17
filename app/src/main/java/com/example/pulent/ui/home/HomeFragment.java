package com.example.pulent.ui.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.example.pulent.databinding.FragmentHomeBinding;
import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.repository.Status;
import com.example.pulent.utils.Constants;
import com.example.pulent.utils.EndlessRecyclerViewScrollListener;
import com.example.pulent.utils.Utilities;
import com.example.pulent.viewmodel.MainActivityViewModel;
import com.example.pulent.viewmodel.MainActivityViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeCardViewAdapterImp {
    private MainActivityViewModel mainActivityViewModel;
    private SongAdapter songAdapter;

    private FragmentHomeBinding fragmentHomeBinding;
    private SongRepository songRepository;

    private FragmentActivity fragmentActivity;
    private NavController navController;

    private final String mediaType = "music";
    private final int limit = 20;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        SongDAO songDAO = AppDatabase.getDatabase(fragmentActivity).songDAO();
        SongApi songApi =  ApiClient.getClient().create(SongApi.class);
        songRepository = new SongRepository(songDAO, songApi);

        initRecyclerView(fragmentActivity);

        initViewModels();

        initTextChangeListener(fragmentHomeBinding.editTextSongSearch);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(fragmentActivity).registerReceiver((mMessageReceiver), new IntentFilter("MyData"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(fragmentActivity).unregisterReceiver((mMessageReceiver));
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null) {
                String type = intent.getExtras().getString("type");
                if (type != null && type.equals(Constants.TYPE_NETWORK_CONECTION)) {
                    setNetworkConnectionStatus(intent.getExtras().getBoolean("connection"));
                }
            }
        }
    };

    /*private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
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
    };*/

    private void setNetworkConnectionStatus(boolean hasNetworkConnection){
        mainActivityViewModel.setIsNetworkAvailable(hasNetworkConnection);
    }

    //Load ViewModel and observer
    private void initViewModels(){

        MainActivityViewModelFactory viewModelFactory = new MainActivityViewModelFactory(songRepository);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        SongSearchQuery songSearchQuery = new SongSearchQuery("", mediaType, 0, false);
        mainActivityViewModel.setSongSearchQuery(songSearchQuery);
        setNetworkConnectionStatus(Utilities.isNetworkAvailable(fragmentActivity));

        mainActivityViewModel.getIsNetworkAvailable().observe(fragmentActivity, hasNetworkConnection -> fragmentHomeBinding.setHasNetworkConnection(hasNetworkConnection));

        mainActivityViewModel.getState().observe(fragmentActivity, state -> {
            fragmentHomeBinding.setState(state);
            if(state.getStatus() == Status.ERROR){
                Toast.makeText(fragmentActivity, state.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        mainActivityViewModel.getLastSongSearchResult().observe(fragmentActivity, songSearchResults -> {
            if(mainActivityViewModel.getLastSongSearchQuery().getValue() != null)
                //if didn't put any text to search or the response cames with no results
                if(songSearchResults.resultCount == 0){
                    //no text in the search bar
                    if(fragmentHomeBinding.editTextSongSearch.getText().toString().length() == 0){
                        fragmentHomeBinding.textViewTitle.setText(getText(R.string.last_searches));
                        AsyncTask.execute(() -> mainActivityViewModel.setLastSongClicked(songRepository.getRecentSongs())
                        );
                    }else{
                        //no results
                        fragmentHomeBinding.textViewTitle.setText(String.format(getString(R.string.there_is_no_results), fragmentHomeBinding.editTextSongSearch.getText().toString()));
                        songAdapter.setSongs(null);
                    }
                }else {
                    fragmentHomeBinding.textViewTitle.setText(getString(R.string.results));
                    if (mainActivityViewModel.getLastSongSearchQuery().getValue().offset == 0) {
                        songAdapter.setSongs(songSearchResults.songs);
                    } else {    // when is getting more data for the paginator
                        songAdapter.addSongs(songSearchResults.songs);
                    }
                }
        });

        mainActivityViewModel.getLastSongsClicked().observe(fragmentActivity, songs -> songAdapter.setSongs(songs));
    }

    private void initRecyclerView(Context context) {
        // bind RecyclerView
        RecyclerView recyclerView = fragmentHomeBinding.songsRecyclerView;
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

        Bundle bundle = new Bundle();
        bundle.putSerializable("SongClass", song);

        HomeFragmentDirections.ActionHomeFragmentToDetailFragment homeFragmentDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(song);
        navController.navigate(homeFragmentDirections);
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
