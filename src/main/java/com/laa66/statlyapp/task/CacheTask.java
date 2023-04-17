package com.laa66.statlyapp.task;

import com.github.benmanes.caffeine.cache.Cache;
import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CacheTask {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 10 21 * * *")
    public void saveCache() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("api");
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        for (Map.Entry<Object, Object> entry: nativeCache.asMap().entrySet()) {
            String[] params = entry.getKey().toString().split("_");
            switch (params[0]) {
                case "getTopTracks" -> userService.saveUserTracks(params[1], params[2], (TopTracksDTO) entry.getValue());
                case "getTopArtists" -> userService.saveUserArtists(params[1], params[2], (TopArtistsDTO) entry.getValue());
                case "getTopGenres" -> userService.saveUserGenres(params[1], params[2], (TopGenresDTO) entry.getValue());
                case "getMainstreamScore" -> userService.saveUserMainstream(params[1], params[2], (MainstreamScoreDTO) entry.getValue());
            }
        }
        cache.invalidate();
    }

}
