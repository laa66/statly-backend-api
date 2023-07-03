package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;

public interface LibraryAnalysisService {

    LibraryAnalysisDTO getLibraryAnalysis(TracksDTO tracksDTO, Long userId);

    GenresDTO getTopGenres(long userId, String range, ArtistsDTO artistsDTO);

}
