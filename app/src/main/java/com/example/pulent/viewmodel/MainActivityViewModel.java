package com.example.pulent.viewmodel;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.models.SongSearchResults;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.models.State;
import com.example.pulent.repository.Status;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<SongSearchResults> lastSongSearchResult;
    private final MutableLiveData<SongSearchQuery> lastSongSearchQuery;
    private final MutableLiveData<State> state;
    private final MutableLiveData<List<Song>> lastSongsClicked;

    private SongRepository songRepository;
    public boolean isNetworkAvailable = true;

    MainActivityViewModel(SongRepository songRepository){
        lastSongSearchResult = new MutableLiveData<>();
        lastSongSearchQuery = new MutableLiveData<>();
        state = new MutableLiveData<>();
        lastSongsClicked = new MutableLiveData<>();

        lastSongSearchResult.setValue(new SongSearchResults(new ArrayList<>(), 0));
        state.postValue(new State(Status.SUCCESS, ""));

        this.songRepository = songRepository;
    }

    public MutableLiveData<List<Song>> getLastSongsClicked() {
        return lastSongsClicked;
    }

    public void setLastSongClicked(List<Song> lastSongsClicked) {
        this.lastSongsClicked.postValue(lastSongsClicked);
    }


    public MutableLiveData<State> getState() {
        return state;
    }

    public LiveData<SongSearchResults> getLastSongSearchResult() {
        return lastSongSearchResult;
    }

    public void setSongSearchQuery(SongSearchQuery songSearchQuery){
        this.lastSongSearchQuery.setValue(songSearchQuery);
    }

    public MutableLiveData<SongSearchQuery> getLastSongSearchQuery() {
        return lastSongSearchQuery;
    }

    public void getMoreSongs(int limit){
        if(lastSongSearchResult.getValue() != null && lastSongSearchQuery.getValue().hasMoreDataToLoad) {
            lastSongSearchQuery.getValue().offset =+ limit;
            getSongs(lastSongSearchQuery.getValue().query, lastSongSearchQuery.getValue().mediaType, lastSongSearchQuery.getValue().offset, limit);
        }
    }
    public void getFirstSongs(String search, String mediaType, int offset, int limit){
        if(isNetworkAvailable) {
            //lastSongSearchResult.setValue(new SongSearchResults(new ArrayList<>(), 0));

            getSongs(search, mediaType, offset, limit);
        }else {
            AsyncTask.execute(() -> {
                List<Song> songs = songRepository.loadSongsFromDb(search);
                SongSearchResults songSearchResults = new SongSearchResults(songs, songs.size());
                lastSongSearchResult.postValue(songSearchResults);
            });
        }
    }

    private void getSongs(String search, String mediaType, int offset, int limit){
        lastSongSearchQuery.getValue().query = search;
        lastSongSearchQuery.getValue().offset = offset;

        state.postValue(new State(Status.LOADING, "Cargando datos..."));

        songRepository.loadSongss(search, mediaType, offset, limit).enqueue(new Callback<SongSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SongSearchResults> call, @NonNull Response<SongSearchResults> response) {
                if(response.isSuccessful()) {
                    SongSearchResults songSearchResults = response.body();
                    if(songSearchResults != null){
                        lastSongSearchQuery.getValue().hasMoreDataToLoad = songSearchResults.resultCount == limit;
                        //songRepository.insert(songSearchResults.songs);
                    }

                    lastSongSearchResult.postValue(songSearchResults);
                    state.postValue(new State(Status.SUCCESS,""));
                }else{
                    if(response.errorBody() != null)
                        state.postValue(new State(Status.ERROR, response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SongSearchResults> call, @NonNull Throwable t) {
                t.getStackTrace();
                state.postValue(new State(Status.ERROR, t.getMessage()));
            }
        });
    }
}
