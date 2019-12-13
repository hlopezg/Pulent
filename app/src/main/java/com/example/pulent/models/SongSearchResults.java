package com.example.pulent.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SongSearchResults {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("results")
    public final List<Song> songs;
    public int resultCount;

    public SongSearchResults(List<Song> songs, int resultCount) {
        this.songs = songs;
        this.resultCount = resultCount;
    }
}
