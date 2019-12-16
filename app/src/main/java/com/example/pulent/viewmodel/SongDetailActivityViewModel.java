package com.example.pulent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pulent.models.Song;
import com.example.pulent.repository.SongRepository;

import java.util.List;

public class SongDetailActivityViewModel extends ViewModel {
    private MutableLiveData<Song> songSelected;
    private MutableLiveData<List<Song>> songList;

    private SongRepository songRepository;

    SongDetailActivityViewModel(SongRepository songRepository) {
        this.songSelected = new MutableLiveData<>();
        this.songList = new MutableLiveData<>();
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


}
