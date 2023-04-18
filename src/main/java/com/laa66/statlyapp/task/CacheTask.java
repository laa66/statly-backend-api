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

@Component
public class CacheTask {

    private final CacheManager cacheManager;
    private final UserService userService;

    public CacheTask(CacheManager cacheManager, UserService userService) {
        this.cacheManager = cacheManager;
        this.userService = userService;
    }

    @Scheduled(cron = "0 57 12 * * *")
    public void saveCache() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("api");
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        nativeCache.asMap().forEach((key, value) -> {
            String[] params = ((String) key).split("_");
            switch (params[0]) {
                case "getTopTracks" -> userService.saveUserTracks(params[1], params[2], (TopTracksDTO) value);
                case "getTopArtists" -> userService.saveUserArtists(params[1], params[2], (TopArtistsDTO) value);
                case "getTopGenres" -> userService.saveUserGenres(params[1], params[2], (TopGenresDTO) value);
                case "getMainstreamScore" -> userService.saveUserMainstream(params[1], params[2], (MainstreamScoreDTO) value);
            }
        });
        cache.invalidate();
    }

}
