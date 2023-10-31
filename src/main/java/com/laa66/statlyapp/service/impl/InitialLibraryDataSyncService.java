package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.LibraryDataSyncService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class InitialLibraryDataSyncService implements LibraryDataSyncService {

    private final SpotifyAPIService spotifyAPIService;
    private final LibraryAnalysisService libraryAnalysisService;
    private final StatsService statsService;

    @Override
    public void synchronizeTracks(long userId) {
        TracksDTO tracksDTO = spotifyAPIService.getTopTracks(userId, "long");
        statsService.saveUserTracks(Map.of(tracksDTO, userId));
    }

    @Override
    public void synchronizeArtists(long userId) {
        ArtistsDTO artistsDTO = spotifyAPIService.getTopArtists(userId, "long");
        statsService.saveUserArtists(Map.of(artistsDTO, userId));
    }

    @Override
    public void synchronizeGenres(long userId) {
        ArtistsDTO artistsDTO = spotifyAPIService.getTopArtists(userId, "long");
        GenresDTO genresDTO = libraryAnalysisService.getTopGenres(userId, "long", artistsDTO);
        statsService.saveUserGenres(Map.of(genresDTO, userId));
    }
}
