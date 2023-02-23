package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.ItemRecentlyPlayed;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;

import java.util.List;

public interface SpotifyApiService {

    List<ItemTopTracks> getTopTracks(String url);

    List<ItemTopArtists> getTopArtists(String url);

    //List<Genre> getTopGenres();

    List<ItemRecentlyPlayed> getRecentlyPlayed();

    void postPlaylist();

}
