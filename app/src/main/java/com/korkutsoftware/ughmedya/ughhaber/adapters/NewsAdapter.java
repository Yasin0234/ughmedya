package com.korkutsoftware.ughmedya.ughhaber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.korkutsoftware.ughmedya.R;
import com.korkutsoftware.ughmedya.ughhaber.models.NewsItem;
import java.util.List;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private OnSaveClickListener onSaveClickListener;

    public interface OnSaveClickListener {
        void onSaveClick(NewsItem newsItem, String mediaUrl);
    }

    public NewsAdapter(List<NewsItem> newsList, OnSaveClickListener onSaveClickListener) {
        this.newsList = newsList;
        this.onSaveClickListener = onSaveClickListener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.sourceTv.setText(item.getSource() != null ? item.getSource() : "Twitter");
        
        if (item.getTitle() != null && !item.getTitle().isEmpty()) {
            holder.titleTv.setVisibility(View.VISIBLE);
            holder.titleTv.setText(item.getTitle());
        } else {
            holder.titleTv.setVisibility(View.GONE);
        }
        
        holder.contentTv.setText(item.getContent());

        List<MediaAdapter.MediaItemData> mediaDataList = new ArrayList<>();
        if (item.getImages() != null) {
            for (String url : item.getImages()) {
                mediaDataList.add(new MediaAdapter.MediaItemData(url, false));
            }
        }
        if (item.getVideos() != null) {
            for (String url : item.getVideos()) {
                mediaDataList.add(new MediaAdapter.MediaItemData(url, true));
            }
        }
        
        // Fallback for old single imageUrl
        if (mediaDataList.isEmpty() && item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            mediaDataList.add(new MediaAdapter.MediaItemData(item.getImageUrl(), false));
        }

        if (!mediaDataList.isEmpty()) {
            holder.mediaRv.setVisibility(View.VISIBLE);
            MediaAdapter mediaAdapter = new MediaAdapter(mediaDataList);
            holder.mediaRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.mediaRv.setAdapter(mediaAdapter);
        } else {
            holder.mediaRv.setVisibility(View.GONE);
        }
        
        holder.saveBtn.setText("Editlemeye Hazırla");
        holder.saveBtn.setOnClickListener(v -> {
            String firstMedia = null;
            if (item.getImages() != null && !item.getImages().isEmpty()) {
                firstMedia = item.getImages().get(0);
            } else if (item.getVideos() != null && !item.getVideos().isEmpty()) {
                firstMedia = item.getVideos().get(0);
            } else if (item.getImageUrl() != null) {
                firstMedia = item.getImageUrl();
            }
            onSaveClickListener.onSaveClick(item, firstMedia);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView sourceTv, titleTv, contentTv;
        RecyclerView mediaRv;
        Button saveBtn;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceTv = itemView.findViewById(R.id.source_tv);
            titleTv = itemView.findViewById(R.id.title_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            mediaRv = itemView.findViewById(R.id.media_recycler_view);
            saveBtn = itemView.findViewById(R.id.save_btn);
        }
    }
}