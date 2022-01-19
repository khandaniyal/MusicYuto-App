package com.example.ezmusicapp.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmusicapp.R;
import com.example.ezmusicapp.interfaces.OnSongChangeListener;
import com.example.ezmusicapp.model.Song;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private ArrayList<Song> songsList;
    private Context c;
    private int playingPos = 0;
    private OnSongChangeListener onSongChangeListener;

    public SongsAdapter(ArrayList<Song> songsList, Context c){
        this.songsList = songsList;
        this.c = c;
        this.onSongChangeListener = ((OnSongChangeListener) c);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.songs, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song list = songsList.get(position);

        if(list.isPlaying()) {
            playingPos = position;
            holder.layout.setBackgroundResource(R.drawable.round_back_blue);
        }else {
            holder.layout.setBackgroundResource(R.drawable.round_back_10);
        }

        //Convert song duration into the correct format
        String duration = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(list.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list.getDuration()))));

        holder.songTitle.setText(list.getTitle());
        holder.songArtist.setText(list.getArtist());
        holder.songDuration.setText(duration);

        holder.layout.setOnClickListener(e->{
            songsList.get(playingPos).setPlaying(false);
            list.setPlaying(true);

            onSongChangeListener.onChanged(position);

            notifyDataSetChanged();
        });

    }

    public void updateList(ArrayList<Song> list){
        songsList = list;
    }

    @Override
    public int getItemCount() { return songsList.size(); }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout layout;
        private TextView songTitle;
        private TextView songArtist;
        private TextView songDuration;

        public ViewHolder(@NonNull View itemView){
          super(itemView);
          layout = itemView.findViewById(R.id.rootLayout);
          songTitle = itemView.findViewById(R.id.txtSongTitle);
          songArtist = itemView.findViewById(R.id.txtSongArtist);
          songDuration = itemView.findViewById(R.id.txtSongDuration);
        }
    }
}
