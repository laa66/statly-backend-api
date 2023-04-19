package com.laa66.statlyapp.task;

import com.github.benmanes.caffeine.cache.Cache;
import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.service.UserService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheTask {

    private final CacheManager cacheManager;
    private final UserService userService;

    public CacheTask(CacheManager cacheManager, UserService userService) {
        this.cacheManager = cacheManager;
        this.userService = userService;
    }

    @Scheduled(cron = "0 38 14 * * *")
    public void saveCache() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("api");
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        Map<TopTracksDTO, Long> tracksDTOMap = new HashMap<>();
        Map<TopArtistsDTO, Long> artistsDTOMap = new HashMap<>();
        Map<TopGenresDTO, Long> genresDTOMap = new HashMap<>();
        Map<MainstreamScoreDTO, Long> mainstreamScoreDTOMap = new HashMap<>();
        nativeCache.asMap().forEach((key, value) -> {
            String[] params = ((String) key).split("_");
            switch (params[0]) {
                case "getTopTracks" -> tracksDTOMap.put(((TopTracksDTO) value), Long.valueOf(params[1]));
                case "getTopArtists" -> artistsDTOMap.put(((TopArtistsDTO) value), Long.valueOf(params[1]));
                case "getTopGenres" -> genresDTOMap.put(((TopGenresDTO) value), Long.valueOf(params[1]));
                case "getMainstreamScore" -> mainstreamScoreDTOMap.put(((MainstreamScoreDTO) value), Long.valueOf(params[1]));
            }
        });
        userService.saveUserTracks(tracksDTOMap);
        userService.saveUserArtists(artistsDTOMap);
        userService.saveUserGenres(genresDTOMap);
        userService.saveUserMainstream(mainstreamScoreDTOMap);
        cache.invalidate();
    }

}
