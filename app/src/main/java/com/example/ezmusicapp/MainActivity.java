package com.example.ezmusicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout searchButton = findViewById(R.id.searchBtn);
        LinearLayout menuBtn = findViewById(R.id.menuBtn);
        RecyclerView songsRV = findViewById(R.id.rvSongList);
        ImageView playButton = findViewById(R.id.imgPlayBtn);
        ImageView previousButton = findViewById(R.id.imgPreviousBtn);
        ImageView nextButton = findViewById(R.id.imgNextBtn);

        //set rv
        songsRV.setHasFixedSize(true);
        songsRV.setLayoutManager(new LinearLayoutManager(this));


    }
}