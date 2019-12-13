package com.example.pulent.models;

import androidx.room.Entity;

@Entity
public class SongSearchQuery {
    public String query;
    public final String mediaType;
    public int offset;
    public boolean hasMoreDataToLoad;

    public SongSearchQuery(String query, String mediaType, int offset, boolean hasMoreDataToLoad) {
        this.query = query;
        this.mediaType = mediaType;
        this.offset = offset;
        this.hasMoreDataToLoad = hasMoreDataToLoad;
    }


}
