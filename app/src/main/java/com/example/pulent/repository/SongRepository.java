package com.example.pulent.repository;

import androidx.lifecycle.LiveData;

import com.example.pulent.api.ApiResponse;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.SongDAO;
import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchResults;
import com.example.pulent.ui.AppExecutors;
import com.example.pulent.utils.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class SongRepository {

    private final SongDAO songDAO;
    private final SongApi songApi;
    private final AppExecutors appExecutors;

    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    public SongRepository(SongDAO songDAO, SongApi songApi, AppExecutors appExecutors) {
        this.songDAO = songDAO;
        this.songApi = songApi;
        this.appExecutors = appExecutors;
    }

    public Call<SongSearchResults> loadSongss(String search, String mediaType, int offset, int limit){
        return songApi.getAllData(search, mediaType, offset, limit);
    }

    public LiveData<Resource<List<Song>>> loadSongs(String search, String mediaType, int limit){
        return new NetworkBoundResource<List<Song>, List<Song>>(appExecutors){

            @Override
            protected boolean shouldFetch(List<Song> data) {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(search);
            }

            @Override
            protected LiveData<List<Song>> loadFromDb() {
                return songDAO.getAll();
            }

            @Override
            protected void saveCallResult(List<Song> songs) {
                songDAO.insert(songs);
            }

            @Override
            protected LiveData<ApiResponse<List<Song>>> createCall() {
                return songApi.getAll(search, mediaType, limit);
            }

            @Override
            protected void onFetchFailed(){
                repoListRateLimit.reset(search);
            }
        }.asLiveData();
    }
}
