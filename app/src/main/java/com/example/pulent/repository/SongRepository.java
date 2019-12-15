package com.example.pulent.repository;

import android.os.AsyncTask;

import com.example.pulent.api.SongApi;
import com.example.pulent.database.SongDAO;
import com.example.pulent.models.Song;
import com.example.pulent.models.SongSearchResults;

import java.util.List;

import retrofit2.Call;

public class SongRepository {
    private final SongDAO songDAO;
    private final SongApi songApi;

    public SongRepository(SongDAO songDAO, SongApi songApi) {
        this.songDAO = songDAO;
        this.songApi = songApi;
    }

    public Call<SongSearchResults> loadSongss(String search, String mediaType, int offset, int limit){
        return songApi.getAllData(search, mediaType, offset, limit);
    }

    public List<Song> getRecentSongs(){
        return songDAO.getRecent();
    }

    public List<Song> loadAllSongs(){
        return songDAO.getAll();
    }

    public List<Song> loadSongsFromDb(String query){
        return songDAO.findByQuery(query);
    }


    public void insert(Song song){
        AsyncTask.execute(() -> {
            songDAO.insert(song);
        });
    }

    public void insert(List<Song> songs){
        AsyncTask.execute(() -> {
            songDAO.insert(songs);
        });
    }

    public void deleteAll(){
        AsyncTask.execute(() -> {
            songDAO.deleteAll();
        });
    }
}
