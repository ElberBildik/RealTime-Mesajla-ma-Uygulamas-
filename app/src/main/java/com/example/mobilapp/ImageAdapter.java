package com.example.mobilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<String> resimUrlListesi;

    public ImageAdapter(Context context, List<String> resimUrlListesi) {
        this.context = context;
        this.resimUrlListesi = resimUrlListesi;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_giris_sayfasi, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String resimUrl = resimUrlListesi.get(position);
        Glide.with(context).load(resimUrl).into(holder.resimImageView);
    }

    @Override
    public int getItemCount() {
        return resimUrlListesi.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView resimImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            resimImageView = itemView.findViewById(R.id.imageView2);
        }
    }
}
