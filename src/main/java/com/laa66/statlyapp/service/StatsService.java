package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;

import java.util.Map;

public interface StatsService {

    void saveUserTracks(Map<TracksDTO, Long> dtoMap);

    void saveUserArtists(Map<ArtistsDTO, Long> dtoMap);

    void saveUserGenres(Map<GenresDTO, Long> dtoMap);

    TracksDTO compareTracks(long userId, TracksDTO dto);

    ArtistsDTO compareArtists(long userId, ArtistsDTO dto);

    GenresDTO compareGenres(long userId, GenresDTO dto);

}
