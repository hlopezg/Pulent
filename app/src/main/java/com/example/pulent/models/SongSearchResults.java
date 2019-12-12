package com.example.pulent.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class SongSearchResults {
    @PrimaryKey
    public final String query;
    public final List<Song> songs;
    public final int resultCount;
    public final int next;

    public SongSearchResults(String query, List<Song> songs, int resultCount, int next) {
        this.query = query;
        this.songs = songs;
        this.resultCount = resultCount;
        this.next = next;
    }
}
