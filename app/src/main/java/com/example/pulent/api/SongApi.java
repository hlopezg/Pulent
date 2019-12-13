package com.example.pulent.api;

import androidx.lifecycle.LiveData;

import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchResults;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SongApi {
    //https://itunes.apple.com/search?term=in+utero&mediaType=music&limit=20
    @GET("search?term={search}&mediaType={mediaType}&limit={limit}")
    LiveData<ApiResponse<List<Song>>> getAll(@Field("search") String search, @Field("mediaType") String mediaType, @Field("limit") int limit);

    /*@GET("search?term={search}&mediaType={mediaType}&limit={limit}")
    Call<List<Song>> getAllData(@Field("search") String search, @Field("mediaType") String mediaType, @Field("limit") int limit);*/

    @GET("search?")
    Call<SongSearchResults> getAllData(@Query("term") String term, @Query("mediaType") String mediaType,@Query("offset") int offset, @Query("limit") int limit);
}
