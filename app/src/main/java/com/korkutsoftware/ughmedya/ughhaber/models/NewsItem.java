package com.korkutsoftware.ughmedya.ughhaber.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

public class NewsItem {
    private String id;
    private String title;
    private String content;
    private String source; // "Twitter", "RSS", etc.
    private String imageUrl;
    private Timestamp timestamp;
    private Timestamp updated_at;
    
    // New fields for bot-collected news
    private List<String> images;
    private List<String> videos;
    private String text;

    public NewsItem() {}

    public NewsItem(String id, String title, String content, String source, String imageUrl, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { 
        if (content != null) return content;
        return text; 
    }
    public void setContent(String content) { this.content = content; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getImageUrl() { 
        if (imageUrl != null) return imageUrl;
        if (images != null && !images.isEmpty()) return images.get(0);
        return null;
    }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public Timestamp getUpdated_at() { return updated_at; }
    public void setUpdated_at(Timestamp updated_at) { this.updated_at = updated_at; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public List<String> getVideos() { return videos; }
    public void setVideos(List<String> videos) { this.videos = videos; }

    @PropertyName("text")
    public String getText() { return text; }
    @PropertyName("text")
    public void setText(String text) { this.text = text; }
}