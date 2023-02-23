package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.*;

public interface SpotifyApiService {

    SpotifyResponseTopTracks getTopTracks(String url);

    SpotifyResponseTopArtists getTopArtists(String url);

    SpotifyResponseTopGenres getTopGenres(String url);

    SpotifyResponseRecentlyPlayed getRecentlyPlayed();

    void postPlaylist();

}
