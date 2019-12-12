package com.example.pulent.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pulent.R;
import com.example.pulent.api.ApiClient;
import com.example.pulent.api.SongApi;
import com.example.pulent.database.AppDatabase;
import com.example.pulent.database.SongDAO;
import com.example.pulent.repository.SongRepository;
import com.example.pulent.ui.AppExecutors;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            SongDAO songDAO = AppDatabase.getDatabase(this).songDAO();
            Retrofit apiClient = ApiClient.getClient();
            SongApi songApi = apiClient.create(SongApi.class);
            AppExecutors appExecutors = new AppExecutors();

            SongRepository songRepository = new SongRepository(songDAO, songApi, appExecutors);
            songRepository.loadSongs("in utero", "music", 20);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
