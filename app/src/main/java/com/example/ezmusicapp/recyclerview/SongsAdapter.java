package com.example.ezmusicapp.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private ArrayList<?> songsList;

    public SongsAdapter(ArrayList<?> songsList){
        this.songsList = songsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() { return 0; }

    /////REMINDER CREATE CUSTOM SONGS CARDVIEW XML TO DISPLAY
    public class ViewHolder extends RecyclerView.ViewHolder{

      public ViewHolder(@NonNull View itemView){

          super(itemView);
      }
    }
}
