package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;

public interface SpotifyAPIService {

    TopTracksDTO getTopTracks(long userId, String range);

    TopArtistsDTO getTopArtists(long userId, String range);

    RecentlyPlayedDTO getRecentlyPlayed();

    MainstreamScoreDTO getMainstreamScore(long userId, String range);

    UserDTO getCurrentUser();

    PlaylistDTO postTopTracksPlaylist(long userId, String range);

}
