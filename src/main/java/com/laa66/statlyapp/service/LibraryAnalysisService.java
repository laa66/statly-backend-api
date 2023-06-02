package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;

public interface LibraryAnalysisService {

    LibraryAnalysisDTO getLibraryAnalysis(TopTracksDTO tracksDTO);

    TopGenresDTO getTopGenres(long userId, String range, TopArtistsDTO artistsDTO);

    double getMainstreamScore(TopTracksDTO tracksDTO);

}
