package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.exchange.*;

public interface SpotifyApiService {

    SpotifyResponseTopTracks getTopTracks(String url);

    SpotifyResponseTopArtists getTopArtists(String url);

    SpotifyResponseTopGenres getTopGenres(String url);

    SpotifyResponseRecentlyPlayed getRecentlyPlayed();

    SpotifyResponseMainstreamScore getMainstreamScore(String url);

    SpotifyResponseId getCurrentUser();

    String postTopTracksPlaylist(String url);

}
