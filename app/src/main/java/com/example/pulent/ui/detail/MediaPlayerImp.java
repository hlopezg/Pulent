package com.example.pulent.ui.detail;

import android.graphics.drawable.Drawable;

import com.example.pulent.models.Song;

public interface MediaPlayerImp {
    void playNewSong(Song song);
    void playAudio(String url);
    void clickOnMedia();
    void resume();
    void stopAudio();
    boolean isPlaying();
    Drawable getPlayDrawable();
    Drawable getPauseDrawable();
}
