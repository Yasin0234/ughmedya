package com.korkutsoftware.ughmedya.ughhaber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.korkutsoftware.ughmedya.R;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private List<MediaItemData> mediaList;

    public static class MediaItemData {
        String url;
        boolean isVideo;

        public MediaItemData(String url, boolean isVideo) {
            this.url = url;
            this.isVideo = isVideo;
        }
    }

    public MediaAdapter(List<MediaItemData> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItemData item = mediaList.get(position);
        if (item.isVideo) {
            holder.imageView.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);
            
            ExoPlayer player = new ExoPlayer.Builder(holder.itemView.getContext()).build();
            holder.playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(item.url);
            player.setMediaItem(mediaItem);
            player.prepare();
        } else {
            holder.playerView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.url)
                    .into(holder.imageView);
        }
    }

    @Override
    public void onViewRecycled(@NonNull MediaViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.getPlayer().release();
            holder.playerView.setPlayer(null);
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PlayerView playerView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.media_image);
            playerView = itemView.findViewById(R.id.media_player);
        }
    }
}