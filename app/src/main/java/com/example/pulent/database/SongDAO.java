package com.example.pulent.database;

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
//collectionName
    @Query("SELECT * FROM Song WHERE artistName LIKE '%' || :query || '%' OR trackName LIKE '%' || :query || '%'")
    List<Song> findByQuery(String query);

    @Query("SELECT * FROM Song WHERE trackId = :trackId")
    Song findSong(long trackId);

    @Query("SELECT * FROM Song LIMIT :limit")
    List<Song> getLast(int limit);

    @Query("SELECT * FROM Song")
    List<Song> getAll();

    @Query("SELECT * FROM Song WHERE collectionId = :collectionId")
    List<Song> findSongsFromAlbmun(long collectionId);

     /*@Query("SELECT * FROM Song")
    Flowable<Song> getFlowableAll();*/
}
