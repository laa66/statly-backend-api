package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;

public interface SpotifyAPIService {

    TopTracksDTO getTopTracks(String username, String url);

    TopArtistsDTO getTopArtists(String username, String url);

    TopGenresDTO getTopGenres(String username, String url);

    RecentlyPlayedDTO getRecentlyPlayed(String username);

    MainstreamScoreDTO getMainstreamScore(String username, String url);

    UserDTO getCurrentUser();

    PlaylistDTO postTopTracksPlaylist(String username, String url);

}
