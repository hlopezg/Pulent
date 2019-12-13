package com.example.pulent.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pulent.models.SongSearchQuery;
import com.example.pulent.models.SongSearchResults;
import com.example.pulent.repository.SongRepository;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<SongSearchResults> lastSongSearchResult;
    private final MutableLiveData<SongSearchQuery> lastSongSearchQuery;
    private SongRepository songRepository;

    MainActivityViewModel(SongRepository songRepository){
        lastSongSearchResult = new MutableLiveData<>();
        lastSongSearchQuery = new MutableLiveData<>();

        lastSongSearchResult.setValue(new SongSearchResults(new ArrayList<>(), 0));

        this.songRepository = songRepository;
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
        lastSongSearchResult.setValue(new SongSearchResults(new ArrayList<>(), 0));

        getSongs(search, mediaType, offset, limit);
    }

    private void getSongs(String search, String mediaType, int offset, int limit){
        lastSongSearchQuery.getValue().query = search;
        lastSongSearchQuery.getValue().offset = offset;

        songRepository.loadSongss(search, mediaType, offset, limit).enqueue(new Callback<SongSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SongSearchResults> call, @NonNull Response<SongSearchResults> response) {
                if(response.isSuccessful()) {
                    SongSearchResults songSearchResults = response.body();
                    if(songSearchResults != null){
                        lastSongSearchQuery.getValue().hasMoreDataToLoad = songSearchResults.resultCount == limit;
                    }

                    lastSongSearchResult.postValue(songSearchResults);
                }else{
                    /*if (response.errorBody() != null) {
                        Log.d("errorBody", response.message());
                    }
                    if(view!= null)
                        view.onFailureTopUp();*/
                }
                //view.hideProgressLayout();
            }

            @Override
            public void onFailure(@NonNull Call<SongSearchResults> call, @NonNull Throwable t) {
                t.getStackTrace();
                /*if(view!= null) {
                    view.onFailureTopUp();
                    view.hideProgressLayout();
                }*/
            }
        });
    }
}
