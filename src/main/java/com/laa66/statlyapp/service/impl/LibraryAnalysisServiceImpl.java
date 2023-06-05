package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LibraryAnalysisServiceImpl implements LibraryAnalysisService {

    private final StatsService statsService;
    private final SpotifyAPIService spotifyAPIService;

    @Override
    public LibraryAnalysisDTO getLibraryAnalysis(TracksDTO tracksDTO) {
        return Optional.ofNullable(tracksDTO).map(
                tracks -> {
                    String tracksIds = getTracksIds(tracksDTO);
                    ResponseTracksAnalysis tracksAnalysis = spotifyAPIService.getTracksAnalysis(tracksIds);
                    Map<String, Double> mapAnalysis = mapAnalysis(tracksAnalysis);
                    addToMap(mapAnalysis, "mainstream", getMainstreamScore(tracksDTO));
                    addToMap(mapAnalysis, "boringness", getBoringness(mapAnalysis));
                    return new LibraryAnalysisDTO(mapAnalysis);
                }
        ).orElseThrow(() -> new RuntimeException("Tracks cannot be null"));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public GenresDTO getTopGenres(long userId, String range, ArtistsDTO artistsDTO) {
        return Optional.ofNullable(artistsDTO)
                .map(ArtistsDTO::getArtists)
                .map(topArtists -> {
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
                            .toList();
                    return statsService.compareGenres(userId, new GenresDTO(transformedGenres, range, null));
                })
                .orElseThrow(() -> new RuntimeException("Artists cannot be null"));
    }

    //helpers
    private double getMainstreamScore(TracksDTO tracksDTO) {
        return tracksDTO.getTracks()
                .stream()
                .mapToInt(Track::getPopularity)
                .average()
                .orElse(0);
    }

    private double getBoringness(Map<String, Double> mapAnalysis) {
        return new BigDecimal(Double.toString(
                mapAnalysis.get("tempo")
                + (mapAnalysis.get("valence") * 100)
                + (mapAnalysis.get("energy") * 100)
                + (mapAnalysis.get("danceability") * 100)))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String getTracksIds(TracksDTO tracksDTO) {
        return tracksDTO.getTracks()
                .stream()
                .map(Track::getId)
                .collect(Collectors.joining(","));
    }

    private Map<String, Double> mapAnalysis(ResponseTracksAnalysis tracksAnalysis) {
        Map<String, Double> analyzedTracks = new HashMap<>();
        int trackCount = tracksAnalysis.getTracksAnalysis().size();

        tracksAnalysis.getTracksAnalysis().forEach(
                track -> {
                    addToMap(analyzedTracks, "acousticness", track.getAcousticness());
                    addToMap(analyzedTracks, "danceability", track.getDanceability());
                    addToMap(analyzedTracks, "energy", track.getEnergy());
                    addToMap(analyzedTracks, "instrumentalness", track.getInstrumentalness());
                    addToMap(analyzedTracks, "liveness", track.getLiveness());
                    addToMap(analyzedTracks, "loudness", track.getLoudness());
                    addToMap(analyzedTracks, "speechiness", track.getSpeechiness());
                    addToMap(analyzedTracks, "tempo", track.getTempo());
                    addToMap(analyzedTracks, "valence", track.getValence());
                }
        );

        analyzedTracks.forEach((key, value) -> {
            double avg = value / trackCount;
            analyzedTracks.put(key, new BigDecimal(Double
                            .toString(avg))
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue());
        });

        return analyzedTracks;
    }

    private void addToMap(Map<String, Double> map, String key, double value) {
        map.merge(key, value, Double::sum);
    }
}
