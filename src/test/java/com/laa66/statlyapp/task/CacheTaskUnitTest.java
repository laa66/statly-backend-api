package com.laa66.statlyapp.task;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.service.StatsService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheTaskUnitTest {

    @Mock
    CacheManager cacheManager;

    @Mock
    StatsService statsService;

    @InjectMocks
    CacheTask cacheTask;

    @Test
    void shouldSaveCache() {
        CaffeineCache cache = new CaffeineCache("api", Caffeine.newBuilder().build());
        cache.put("getTopTracks_1_short", new TopTracksDTO());
        cache.put("getTopArtists_1_short", new TopArtistsDTO());
        cache.put("getTopGenres_1_short", new TopGenresDTO());
        cache.put("getMainstreamScore_1_short", new MainstreamScoreDTO());

        assertEquals(4, cache.getNativeCache().asMap().size());
        when(cacheManager.getCache("api")).thenReturn(cache);
        cacheTask.saveCache();

        verify(statsService, times(1)).saveUserTracks(anyMap());
        verify(statsService, times(1)).saveUserArtists(anyMap());
        verify(statsService, times(1)).saveUserGenres(anyMap());
        verify(statsService, times(1)).saveUserMainstream(anyMap());
        assertEquals(0, cache.getNativeCache().asMap().size());
    }
}