package com.example.ezmusicapp.model;

import android.net.Uri;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Song {
    private String title, artist, duration;
    private boolean isPlaying;
    private Uri songFile;

    public Song(String title, String artist, String duration, boolean isPlaying, Uri songFile) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.songFile = songFile;
    }
}
