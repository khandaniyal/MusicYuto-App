package com.example.ezmusicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezmusicapp.interfaces.OnSongChangeListener;
import com.example.ezmusicapp.model.Song;
import com.example.ezmusicapp.recyclerview.SongsAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnSongChangeListener {
    private ArrayList<Song> songList;
    private RecyclerView songsRV;
    private MediaPlayer songPlayer;
    private TextView startTime, endTime;
    private boolean isPlaying = false;
    private SeekBar songBar;
    private ImageView playPauseImg;
    private Timer timer;
    private CardView playPauseCard;
    private int currentSongPos = 0;
    private SongsAdapter songsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decodeView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decodeView.setSystemUiVisibility(options);
        setContentView(R.layout.activity_main);

        LinearLayout searchButton = findViewById(R.id.searchBtn);
        LinearLayout menuBtn = findViewById(R.id.menuBtn);
        songsRV = findViewById(R.id.rvSongList);
        playPauseImg = findViewById(R.id.imgPlayBtn);
        ImageView previousImg = findViewById(R.id.imgPreviousBtn);
        ImageView nextImg = findViewById(R.id.imgNextBtn);
        playPauseCard = findViewById(R.id.playPause);
        startTime = findViewById(R.id.txtMinStart);
        endTime = findViewById(R.id.txtMinEnd);
        songBar = findViewById(R.id.seekbar);

        //set rv layout
        songsRV.setHasFixedSize(true);
        songsRV.setLayoutManager(new LinearLayoutManager(this));

        //initialize media player
        songPlayer = new MediaPlayer();

        //ask the user for permissions
        runtimePermission();

        //user interaction
        previousImg.setOnClickListener(e->{
            int goPrevSong = currentSongPos-1;

            if(goPrevSong < 0){
                goPrevSong = songList.size()-1;
            }
            songList.get(currentSongPos).setPlaying(false);
            songList.get(goPrevSong).setPlaying(true);

            songsAdapter.updateList(songList);

            songsRV.scrollToPosition(goPrevSong);
            onChanged(goPrevSong);

        });

        nextImg.setOnClickListener(e->{
            int goNextSong = currentSongPos+1;

            if(goNextSong >= songList.size()){
                goNextSong = 0;
            }
            songList.get(currentSongPos).setPlaying(false);
            songList.get(goNextSong).setPlaying(true);
            songsAdapter.updateList(songList);

            songsRV.scrollToPosition(goNextSong);
            onChanged(goNextSong);
        });
        playPauseCard.setOnClickListener(e->{
            if(isPlaying) {
                isPlaying = false;
                songPlayer.pause();
                playPauseImg.setImageResource(R.drawable.ic_play_arrow);
            }else{
                isPlaying = true;
                songPlayer.start();
                playPauseImg.setImageResource(R.drawable.ic_pause);
            }
        });

        songBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userInput) {
                if(userInput)
                    if(isPlaying) songPlayer.seekTo(progress);
                    else songBar.setProgress(0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }
    //sergi eres un enfermo
    public void runtimePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 11);
        }

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                loadAudioFiles();

            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getApplicationContext(), "You declined the permission", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) { permissionToken.continuePermissionRequest(); }
        }).check();
    }

    public void loadAudioFiles(){
        songList = SongPlayer.getAudioFiles(this, songList, getContentResolver());
        songsAdapter = new SongsAdapter(songList, this);
        songsRV.setAdapter(songsAdapter);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onChanged(int pos) {
        if(songPlayer.isPlaying()){
            songPlayer.pause();
            songPlayer.reset();
        }

        songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            songPlayer.setDataSource(MainActivity.this, songList.get(pos).getSongFile());
                            songPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "unable to load track", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

        songPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int getTotalDuration = mediaPlayer.getDuration();

                //Convert song duration into the correct format
                String duration = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(getTotalDuration))),
                        TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(String.valueOf(getTotalDuration))),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(getTotalDuration)))));

                endTime.setText(duration);
                isPlaying = true;

                mediaPlayer.start();

                songBar.setMax(getTotalDuration);

                playPauseImg.setImageResource(R.drawable.ic_pause);
            }
        });
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int generateCurrentDuration = songPlayer.getCurrentPosition();
                        //Convert song duration into the correct format
                        String duration = String.format(Locale.getDefault(), "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(generateCurrentDuration))),
                                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(String.valueOf(generateCurrentDuration))),
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(generateCurrentDuration)))));

                        songBar.setProgress(generateCurrentDuration);
                        startTime.setText(duration);
                    }
                });
            }
        }, 1000, 1000);

        songPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                songPlayer.reset();

                timer.purge();
                timer.cancel();

                isPlaying = false;

                playPauseImg.setImageResource(R.drawable.ic_play_arrow);

                songBar.setProgress(0);

            }
        });
    }
}