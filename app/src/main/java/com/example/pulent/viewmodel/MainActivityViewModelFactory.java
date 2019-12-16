package com.example.pulent.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.pulent.repository.SongRepository;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {
    private final SongRepository songRepository;

    public MainActivityViewModelFactory(SongRepository songRepository){
        this.songRepository = songRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainActivityViewModel.class))
            return  (T) new MainActivityViewModel(songRepository);
        else if(modelClass.isAssignableFrom(SongDetailActivityViewModel.class))
            return  (T) new SongDetailActivityViewModel(songRepository);
        else{
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
