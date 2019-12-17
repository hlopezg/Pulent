package com.example.pulent.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pulent.R;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_content, fragment)
                    .commit();
        }*/
    }

    /*public void navigateToProductDetail(String productId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(DetailFragment.TAG)
                .replace(R.id.main_content, fragment, ProductDetailsFragment.TAG)
                .commit();
    }*/

}
