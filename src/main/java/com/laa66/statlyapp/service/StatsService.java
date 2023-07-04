package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import org.springframework.data.util.Pair;

import java.util.Map;

public interface StatsService {

    TracksDTO getUserTracks(long userId, String range);

    ArtistsDTO getUserArtists(long userId, String range);

    void saveUserTracks(Map<TracksDTO, Long> dtoMap);

    void saveUserArtists(Map<ArtistsDTO, Long> dtoMap);

    void saveUserGenres(Map<GenresDTO, Long> dtoMap);

    void saveUserStats(long userId, Map<String, Double> statsMap);

    TracksDTO compareTracks(long userId, TracksDTO dto);

    ArtistsDTO compareArtists(long userId, ArtistsDTO dto);

    GenresDTO compareGenres(long userId, GenresDTO dto);

    Pair<Integer, Integer> matchTracks(long userId, long matchUserId);

}
