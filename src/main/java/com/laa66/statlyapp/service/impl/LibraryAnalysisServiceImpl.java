package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class LibraryAnalysisServiceImpl implements LibraryAnalysisService {

    private final StatsService statsService;
    private final SpotifyAPIService spotifyAPIService;
    private final SocialService socialService;

    /**
     *
     * @param tracksDTO DTO that contains tracks to analysis
     * @param userId null if analysis result should not be saved
     * @return audio analysis result based on passed object
     */
    @Override
    public AnalysisDTO getTracksAnalysis(TracksDTO tracksDTO, @Nullable Long userId) {
        return Optional.ofNullable(tracksDTO).map(
                tracks -> {
                    ResponseTracksAnalysis tracksAnalysis = spotifyAPIService.getTracksAnalysis(tracks);
                    Map<String, Double> mapAnalysis = getMapAnalysis(tracksAnalysis);
                    addToMap(mapAnalysis, "boringness", getBoringness(mapAnalysis));
                    addToMap(mapAnalysis, "mainstream", getMainstreamScore(tracks));
                    List<Image> images = tracks.getTracks().stream()
                            .map(track -> Optional
                                    .ofNullable(track.getAlbum().getImages().get(0))
                                    .orElseThrow(() -> new RuntimeException("Image cannot be null")))
                            .limit(22)
                            .toList();
                    if (userId != null) statsService.saveUserStats(userId, mapAnalysis);
                    return new AnalysisDTO(mapAnalysis, images);
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

    @Override
    public Map<String, Double> getUsersMatching(long userId, long matchUserId) {
        Pair<Integer, Integer> track = statsService.matchTracks(userId, matchUserId);
        Pair<Integer, Integer> artist = statsService.matchArtists(userId, matchUserId);
        Pair<Integer, Integer> genre = statsService.matchGenres(userId, matchUserId);
        Map<String, Double> matchMap = new HashMap<>(Map.of(
                "track", roundHalfUp(((double) track.getFirst() / track.getSecond()) * 100),
                "artist", roundHalfUp(((double) artist.getFirst() / artist.getSecond()) * 100),
                "genre", roundHalfUp(((double) genre.getFirst() / genre.getSecond()) * 100)));
        matchMap.put("overall", roundHalfUp(matchMap.get("track") * .35 + matchMap.get("artist") * .35 + matchMap.get("genre") * .30));
        return matchMap;
    }

    @Override
    public BattleResultDTO createPlaylistBattle(long userId, long battleUserId,
                                              TracksDTO playlist, TracksDTO battlePlaylist) {
        AnalysisDTO playlistAnalysis = getTracksAnalysis(playlist, null);
        AnalysisDTO battlePlaylistAnalysis = getTracksAnalysis(battlePlaylist, null);
        Battler user = new Battler(userId, playlistAnalysis);
        Battler battleUser = new Battler(battleUserId, battlePlaylistAnalysis);
        return Optional.ofNullable(user.battle(battleUser))
                .map(winnerLoser -> {
                    Battler winner = winnerLoser.getFirst();
                    Battler loser = winnerLoser.getSecond();
                    double diff = winner.getDifference(loser);
                    socialService.updatePoints(winner.getId(), (int) diff);
                    socialService.updatePoints(loser.getId(), (int) -diff);
                    return new BattleResultDTO(
                            socialService.getUserProfile(winner.getId()),
                            socialService.getUserProfile(loser.getId()),
                            winner,
                            loser,
                            diff
                    );
                }).orElse(new BattleResultDTO(
                        null, null,
                        user, battleUser,0));
    }

    private double getMainstreamScore(TracksDTO tracksDTO) {
        return roundHalfUp(tracksDTO.getTracks()
                        .stream()
                        .mapToInt(Track::getPopularity)
                        .average()
                        .orElse(0));
    }

    private double getBoringness(Map<String, Double> mapAnalysis) {
        return !mapAnalysis.isEmpty() ? roundHalfUp(
                mapAnalysis.get("tempo")
                        + (mapAnalysis.get("valence"))
                        + (mapAnalysis.get("energy"))
                        + (mapAnalysis.get("danceability"))) : 0.;
    }

    private Map<String, Double> getMapAnalysis(ResponseTracksAnalysis tracksAnalysis) {
        Map<String, Double> analyzedTracks = new HashMap<>();
        int trackCount = tracksAnalysis.getTracksAnalysis().size();

        tracksAnalysis.getTracksAnalysis().forEach(
                nullableTrack -> {
                    Optional.ofNullable(nullableTrack)
                            .ifPresent(track -> {
                                addToMap(analyzedTracks, "acousticness", track.getAcousticness() * 100);
                                addToMap(analyzedTracks, "danceability", track.getDanceability() * 100);
                                addToMap(analyzedTracks, "energy", track.getEnergy() * 100);
                                addToMap(analyzedTracks, "instrumentalness", track.getInstrumentalness() * 100);
                                addToMap(analyzedTracks, "liveness", track.getLiveness() * 100);
                                addToMap(analyzedTracks, "loudness", track.getLoudness());
                                addToMap(analyzedTracks, "speechiness", track.getSpeechiness() * 100);
                                addToMap(analyzedTracks, "tempo", track.getTempo());
                                addToMap(analyzedTracks, "valence", track.getValence() * 100);
                            });
                }
        );

        return analyzedTracks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        value -> roundHalfUp((value.getValue() / trackCount)),
                        (first, conflict) -> first,
                        LinkedHashMap::new));
    }

    private double roundHalfUp(double num) {
        try {
            return new BigDecimal(Double.toString(num))
                    .setScale(0, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (NumberFormatException e) {
            return 0.;
        }
    }

    private void addToMap(Map<String, Double> map, String key, double value) {
        map.merge(key, value, Double::sum);
    }
}
