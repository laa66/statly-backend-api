package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;

import java.util.Map;

public interface StatsService {

    void saveUserTracks(Map<TopTracksDTO, Long> dtoMap);

    void saveUserArtists(Map<TopArtistsDTO, Long> dtoMap);

    void saveUserGenres(Map<TopGenresDTO, Long> dtoMap);

    void saveUserMainstream(Map<MainstreamScoreDTO, Long> dtoMap);

    TopTracksDTO compareTracks(long userId, TopTracksDTO dto);

    TopArtistsDTO compareArtists(long userId, TopArtistsDTO dto);

    TopGenresDTO compareGenres(long userId, TopGenresDTO dto);

    MainstreamScoreDTO compareMainstream(long userId, MainstreamScoreDTO dto);

}
