package com.learn.youtubedemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.learn.youtubedemo.R;
import com.learn.youtubedemo.model.VidData;

import java.util.List;

public class VidListAdapter extends RecyclerView.Adapter<VidListAdapter.MyViewHolder> {

    private List<VidData> VidDatasList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textVidTitle);
            genre = (TextView) view.findViewById(R.id.textVidDesc);
        }
    }


    public VidListAdapter(List<VidData> VidDatasList) {
        this.VidDatasList = VidDatasList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vidlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VidData VidData = VidDatasList.get(position);
        holder.title.setText(VidData.getVidTitle());
        holder.genre.setText(VidData.getVidDescr());
    }

    @Override
    public int getItemCount() {
        return VidDatasList.size();
    }
}