package com.example.pulent.api;

import androidx.lifecycle.LiveData;

import com.example.pulent.models.Song;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.GET;

public interface SongApi {
    //https://itunes.apple.com/search?term=in+utero&mediaType=music&limit=20
    @GET("search?term={search}&mediaType={mediaType}&limit={limit}")
    LiveData<ApiResponse<List<Song>>> getAll(@Field("search") String search, @Field("mediaType") String mediaType, @Field("limit") int limit);
}
