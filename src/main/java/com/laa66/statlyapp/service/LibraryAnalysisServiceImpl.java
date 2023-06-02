package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LibraryAnalysisServiceImpl implements LibraryAnalysisService {

    private final StatsService statsService;

    @Override
    public LibraryAnalysisDTO getLibraryAnalysis(TopTracksDTO tracksDTO) {
        return null;
    }

    @Override
    public double getMainstreamScore(TopTracksDTO tracksDTO) {
        return 0;
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopGenresDTO getTopGenres(long userId, String range, TopArtistsDTO artistsDTO) {
        List<ItemTopArtists> topArtists = artistsDTO.getItemTopArtists();
        List<Genre> sliceGenres = topArtists
                .stream()
                .flatMap(artist -> artist.getGenres().stream())
                .collect(Collectors.collectingAndThen(Collectors.toMap(Function.identity(), genre -> 1, Integer::sum),
                        stringIntegerMap -> stringIntegerMap.entrySet()
                                .stream()
                                .map(entry -> new Genre(entry.getKey(), entry.getValue()))
                                .sorted(Comparator.reverseOrder())
                                .limit(10)
                                .toList()
                ));
        double sum = sliceGenres
                .stream()
                .mapToInt(Genre::getScore)
                .sum();
        List<Genre> transformedGenres = sliceGenres.stream()
                .map(item -> new Genre(item.getGenre(), (int) ((item.getScore() / sum) * 100)))
                .collect(Collectors.toList());
        return statsService.compareGenres(userId, new TopGenresDTO(transformedGenres, range, null));
    }
}
