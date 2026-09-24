package com.korkutsoftware.ughmedya.ughhaber.api;

import com.korkutsoftware.ughmedya.ughhaber.models.TwitterResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import retrofit2.http.Query;

public interface TwitterApi {
    @GET("2/users/{id}/tweets")
    Call<TwitterResponse> getUserTweets(
        @Path("id") String userId,
        @Header("Authorization") String bearerToken,
        @Query("expansions") String expansions,
        @Query("media.fields") String mediaFields,
        @Query("tweet.fields") String tweetFields
    );

    @GET("2/users/by/username/{username}")
    Call<com.google.gson.JsonObject> getUserIdByUsername(
        @Path("username") String username,
        @Header("Authorization") String bearerToken
    );
}