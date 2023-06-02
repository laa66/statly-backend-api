package com.laa66.statlyapp.task;

import com.github.benmanes.caffeine.cache.Cache;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CacheTask {

    private final CacheManager cacheManager;
    private final StatsService statsService;

    @Scheduled(cron = "0 59 21 * * *")
    public void saveCache() {
        Optional.ofNullable((CaffeineCache) cacheManager.getCache("api"))
                .ifPresentOrElse(caffeineCache -> {
                    Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                    Map<TopTracksDTO, Long> tracksDTOMap = new HashMap<>();
                    Map<TopArtistsDTO, Long> artistsDTOMap = new HashMap<>();
                    Map<TopGenresDTO, Long> genresDTOMap = new HashMap<>();
                    nativeCache.asMap().forEach((key, value) -> {
                        String[] params = ((String) key).split("_");
                        switch (params[0]) {
                            case "getTopTracks" -> tracksDTOMap.put(((TopTracksDTO) value), Long.valueOf(params[1]));
                            case "getTopArtists" -> artistsDTOMap.put(((TopArtistsDTO) value), Long.valueOf(params[1]));
                            case "getTopGenres" -> genresDTOMap.put(((TopGenresDTO) value), Long.valueOf(params[1]));
                        }
                    });
                    statsService.saveUserTracks(tracksDTOMap);
                    statsService.saveUserArtists(artistsDTOMap);
                    statsService.saveUserGenres(genresDTOMap);
                    caffeineCache.invalidate();
                    log.info("-->> API Cache saved to database and cleared.");

                    },
                        () -> log.error("-->> API Cache will not be saved.")
                );
    }

}
