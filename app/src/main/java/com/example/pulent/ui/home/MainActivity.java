package com.example.pulent.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pulent.R;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*setSupportActionBar(activityMainBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }*/

        if (savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_graph, fragment)
                    .commit();
        }

    }

}
