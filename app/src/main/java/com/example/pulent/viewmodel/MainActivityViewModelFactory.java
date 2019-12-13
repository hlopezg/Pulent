package com.example.pulent.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.pulent.repository.SongRepository;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {
    private final SongRepository homeNotificationRepository;

    public MainActivityViewModelFactory(SongRepository homeNotificationRepository){
        this.homeNotificationRepository = homeNotificationRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainActivityViewModel.class))
            return  (T) new MainActivityViewModel(homeNotificationRepository);
        /*else if(modelClass.isAssignableFrom(HomeViewModel.class))
            return  (T) new HomeViewModel(repository);*/
        else{
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
