package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;

public interface SpotifyAPIService {

    TopTracksDTO getTopTracks(String email, String range);

    TopArtistsDTO getTopArtists(String email, String range);

    TopGenresDTO getTopGenres(String email, String range);

    RecentlyPlayedDTO getRecentlyPlayed(String email);

    MainstreamScoreDTO getMainstreamScore(String email, String range);

    UserDTO getCurrentUser();

    PlaylistDTO postTopTracksPlaylist(String email, String range);

}
