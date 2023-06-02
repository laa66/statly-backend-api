package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.ResponseTracksAnalysis;

public interface SpotifyAPIService {

    TopTracksDTO getTopTracks(long userId, String range);

    TopArtistsDTO getTopArtists(long userId, String range);

    RecentlyPlayedDTO getRecentlyPlayed();

    ResponseTracksAnalysis getTracksAnalysis(String tracksIds);

    UserDTO getCurrentUser();

    PlaylistDTO postTopTracksPlaylist(long userId, String range);

}
