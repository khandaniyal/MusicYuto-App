package com.example.ezmusicapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmusicapp.interfaces.OnSongChangeListener;
import com.example.ezmusicapp.model.Song;
import com.example.ezmusicapp.recyclerview.SongsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SongPlayer{
    private MediaPlayer songPlayer;

    @SuppressLint("Range")
    public static ArrayList<Song> getAudioFiles(Context context, ArrayList<Song> songList, ContentResolver contentResolver) {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String select = MediaStore.Audio.Media.DATA + " LIKE?";
        String orderBy = MediaStore.Audio.Media.DATE_TAKEN + " DESC";
        //first getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, select , new String[]{"%.mp3%"}, orderBy);

        if(cursor == null) Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

        if(!cursor.moveToNext()) Toast.makeText(context, "Cannot locate audio files", Toast.LENGTH_SHORT).show();

        if(cursor != null && cursor.getCount() > 0){
            songList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                String duration = "00:00";

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                songList.add(new Song(title, artist, duration, false, songUri));
            }

        }
        cursor.close();
        return songList;
   }
}
