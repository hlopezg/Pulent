package com.example.pulent.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchResults;
import com.example.pulent.repository.SongRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongDetailActivityViewModel extends ViewModel {
    private MutableLiveData<Song> songSelected;
    private MutableLiveData<List<Song>> songList;

    private SongRepository songRepository;

    SongDetailActivityViewModel(SongRepository songRepository) {
        this.songSelected = new MutableLiveData<>();
        this.songList = new MutableLiveData<>();

        this.songRepository = songRepository;
    }

    public MutableLiveData<Song> getSongSelected() {
        return songSelected;
    }

    public void setSongSelected(Song songSelected) {
        this.songSelected.postValue(songSelected);
    }

    public MutableLiveData<List<Song>> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList.postValue(songList);
    }

    public void getSongs(long collectionId, String entity){

        songRepository.loadAllSongsFromCollectionId(collectionId, entity).enqueue(new Callback<SongSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SongSearchResults> call, @NonNull Response<SongSearchResults> response) {
                if(response.isSuccessful()) {
                    SongSearchResults songSearchResults = response.body();
                    if(songSearchResults != null){
                        songList.postValue(songSearchResults.songs);
                    }
                }else{
                    if(response.errorBody() != null){

                    }
                        //state.postValue(new State(Status.ERROR, response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SongSearchResults> call, @NonNull Throwable t) {
                t.getStackTrace();
                //state.postValue(new State(Status.ERROR, t.getMessage()));
            }
        });
    }


}
