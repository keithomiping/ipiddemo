package com.ipid.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder>{

    Context context;
    int list[];

    public SliderAdapter(Context context, int[] list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SliderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.MyViewHolder holder, int position) {
        holder.bannerImageView.setBackgroundResource(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.banner_image);
        }
    }
}
