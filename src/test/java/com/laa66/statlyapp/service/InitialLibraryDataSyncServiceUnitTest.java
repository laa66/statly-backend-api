package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import com.laa66.statlyapp.service.impl.InitialLibraryDataSyncService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialLibraryDataSyncServiceUnitTest {

    @Mock
    SpotifyAPIService spotifyAPIService;

    @Mock
    LibraryAnalysisService analysisService;

    @Mock
    StatsService statsService;

    @Mock
    TracksDTO mockTracksDTO;

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
}