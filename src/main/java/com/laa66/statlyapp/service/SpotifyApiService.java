package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.Item;

import java.util.List;

public interface SpotifyApiService {

    List<Item> getTopTracks(String url);

    List<Item> getTopArtists(String url);

    //List<Genre> getTopGenres();

    List<Item> getRecentlyPlayed();

    void postPlaylist();

}
