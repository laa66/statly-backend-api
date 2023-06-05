package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.Playlist;
import com.laa66.statlyapp.model.PlaylistInfo;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;

public interface SpotifyAPIService {

    TopTracksDTO getTopTracks(long userId, String range);

    TopArtistsDTO getTopArtists(long userId, String range);

    RecentlyPlayedDTO getRecentlyPlayed();

    ResponseTracksAnalysis getTracksAnalysis(String tracksIds);

    ResponsePlaylists getUserPlaylists();

    Playlist getPlaylistTracks(PlaylistInfo playlistInfo, String country);

    UserDTO getCurrentUser();

    PlaylistDTO postTopTracksPlaylist(long userId, String range);

}
