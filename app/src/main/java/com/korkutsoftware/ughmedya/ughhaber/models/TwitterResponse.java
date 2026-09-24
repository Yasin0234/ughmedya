package com.korkutsoftware.ughmedya.ughhaber.models;

import java.util.List;

public class TwitterResponse {
    public List<Tweet> data;
    public Includes includes;

    public static class Tweet {
        public String id;
        public String text;
        public Attachments attachments;
    }

    public static class Attachments {
        public List<String> media_keys;
    }

    public static class Includes {
        public List<Media> media;
    }

    public static class Media {
        public String media_key;
        public String type; // "photo", "video", "animated_gif"
        public String url; // for photos
        public String preview_image_url; // for videos/gifs
    }
}