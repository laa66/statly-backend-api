package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;

import java.util.List;

public interface SpotifyApiService {

    List<Track> getTopTracks();

    List<Artist> getTopArtists();

    //List<Genre> getTopGenres();

    List<Track> getRecentlyPlayed();

    void postPlaylist();

}
