package com.example.pulent.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pulent.models.Song;

import java.util.List;

@Dao
public interface SongDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Song> songs);

    @Update
    void update(List<Song> songs);

    @Delete
    void delete(Song song);

    @Query("DELETE FROM Song")
    void deleteAll();

    @Query("SELECT * FROM Song WHERE artistName = :query OR trackName = :query")
    List<Song> findByQuery(String query);

    @Query("SELECT * FROM Song WHERE trackId = :trackId")
    LiveData<Song> findSong(long trackId);

    @Query("SELECT * FROM Song")
    LiveData<List<Song>> getAll();

     /*@Query("SELECT * FROM Song")
    Flowable<Song> getFlowableAll();*/
}
