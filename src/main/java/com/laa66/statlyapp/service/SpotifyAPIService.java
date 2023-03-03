package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.exchange.*;

public interface SpotifyApiService {

    SpotifyResponseTopTracks getTopTracks(String username, String url);

    SpotifyResponseTopArtists getTopArtists(String username, String url);

    SpotifyResponseTopGenres getTopGenres(String username, String url);

    SpotifyResponseRecentlyPlayed getRecentlyPlayed(String username);

    SpotifyResponseMainstreamScore getMainstreamScore(String username, String url);

    SpotifyResponseId getCurrentUser();

    String postTopTracksPlaylist(String username, String url);

}
