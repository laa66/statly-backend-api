package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.service.impl.InitialLibraryDataSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InitialLibraryDataSyncServiceUnitTest {

    @Mock
    SpotifyAPIService spotifyAPIService;

    @Mock
    LibraryAnalysisService analysisService;

    @Mock
    StatsService statsService;

    @Mock
    ArtistsDTO mockArtistsDTO;

    @Mock
    TracksDTO mockTracksDTO;

    @Mock
    GenresDTO mockGenresDTO;

    @InjectMocks
    InitialLibraryDataSyncService dataSyncService;



    @Test
    void shouldSynchronizeTracks() {
        when(spotifyAPIService.getTopTracks(anyLong(), eq("long"))).thenReturn(mockTracksDTO);
        dataSyncService.synchronizeTracks(1L);
        verify(statsService, times(1))
                .saveUserTracks(argThat(arg -> arg.containsKey(mockTracksDTO)
                && arg.containsValue(1L)
                && arg.keySet().size() == 1));
    }

    @Test
    void shouldSynchronizeArtists() {
        when(spotifyAPIService.getTopArtists(anyLong(), eq("long"))).thenReturn(mockArtistsDTO);
        dataSyncService.synchronizeArtists(1L);
        verify(statsService, times(1))
                .saveUserArtists(argThat(arg -> arg.containsKey(mockArtistsDTO)
                && arg.containsValue(1L)
                && arg.keySet().size() == 1));
    }

    @Test
    void shouldSynchronizeGenres() {
        when(spotifyAPIService.getTopArtists(anyLong(), eq("long"))).thenReturn(mockArtistsDTO);
        when(analysisService.getTopGenres(anyLong(), eq("long"), eq(mockArtistsDTO)))
                .thenReturn(mockGenresDTO);
        dataSyncService.synchronizeGenres(1L);
        verify(statsService, times(1))
                .saveUserGenres(argThat(arg -> arg.containsKey(mockGenresDTO)
                && arg.containsValue(1L)
                && arg.keySet().size() == 1));
    }

    @Test
    void shouldIsLibraryDataSynchronizedTrue() {
        when(statsService.isTrackSynchronized(1L)).thenReturn(true);
        when(statsService.isArtistSynchronized(1L)).thenReturn(true);
        when(statsService.isGenreSynchronized(1L)).thenReturn(true);
        boolean userLibrarySynchronized = dataSyncService.isLibraryDataSynchronized(1L);
        assertTrue(userLibrarySynchronized);
    }

    @Test
    void shouldIsLibraryDataSynchronizedTrackFalse() {
        when(statsService.isTrackSynchronized(1L)).thenReturn(false);
        boolean userLibrarySynchronized = dataSyncService.isLibraryDataSynchronized(1L);
        assertFalse(userLibrarySynchronized);
    }

    @Test
    void shouldIsLibraryDataSynchronizedArtistFalse() {
        when(statsService.isTrackSynchronized(1L)).thenReturn(true);
        when(statsService.isArtistSynchronized(1L)).thenReturn(false);
        boolean userLibrarySynchronized = dataSyncService.isLibraryDataSynchronized(1L);
        assertFalse(userLibrarySynchronized);
    }

    @Test
    void shouldIsLibrarySynchronizedGenreFalse() {
        when(statsService.isTrackSynchronized(1L)).thenReturn(true);
        when(statsService.isArtistSynchronized(1L)).thenReturn(true);
        when(statsService.isGenreSynchronized(1L)).thenReturn(false);
        boolean userLibrarySynchronized = dataSyncService.isLibraryDataSynchronized(1L);
        assertFalse(userLibrarySynchronized);
    }


}