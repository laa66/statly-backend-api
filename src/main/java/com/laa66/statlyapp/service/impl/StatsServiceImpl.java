package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.repository.ArtistRepository;
import com.laa66.statlyapp.repository.GenreRepository;
import com.laa66.statlyapp.repository.TrackRepository;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    @Override
    public TracksDTO getUserTracks(long userId, String range) {
        return trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, range)
                .map(item -> {
                    List<Track> tracks = item.getTracks().entrySet().stream()
                            .sorted(Map.Entry.comparingByValue())
                            .map(entry -> {
                                String[] artistTitle = entry.getKey().split("_");
                                return new Track(List.of(new Artist(artistTitle[0])), artistTitle[1]);
                            }).toList();
                    return new TracksDTO(tracks, Integer.toString(tracks.size()), range, item.getDate());
                }).orElse(new TracksDTO(null, "0", range, null));
    }

    @Override
    public ArtistsDTO getUserArtists(long userId, String range) {
        return artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, range)
                .map(item -> {
                    List<Artist> artists = item.getArtists().entrySet().stream()
                            .sorted(Map.Entry.comparingByValue())
                            .map(entry -> new Artist(entry.getKey()))
                            .toList();
                    return new ArtistsDTO(Integer.toString(artists.size()), artists, range, item.getDate());
                }).orElse(new ArtistsDTO("0", null, range, null));
    }

    @Override
    public void saveUserTracks(Map<TracksDTO, Long> dtoMap) {
        List<UserTrack> userTrackList = dtoMap.entrySet().stream().map(entry -> {
            AtomicInteger counter = new AtomicInteger(1);
            Map<String, Integer> tracks = entry.getKey()
                    .getTracks()
                    .stream()
                    .collect(Collectors
                            .toMap(item -> {
                                List<Artist> artists = item.getArtists();
                                return (artists != null && !artists.isEmpty() ? artists.get(0).getName() : null) + "_" + item.getName(); //added null check
                            }, s -> counter.getAndIncrement()));
            return new UserTrack(0, entry.getValue(), entry.getKey().getRange(), tracks, LocalDate.now());
        }).toList();
        trackRepository.saveAll(userTrackList);
    }

    @Override
    public void saveUserArtists(Map<ArtistsDTO, Long> dtoMap) {
        List<UserArtist> userArtistList = dtoMap.entrySet().stream().map(entry -> {
           AtomicInteger counter = new AtomicInteger(1);
           Map<String, Integer> artists = entry.getKey()
                   .getArtists()
                   .stream()
                   .collect(Collectors
                           .toMap(Artist::getName, s -> counter.getAndIncrement()));
           return new UserArtist(0, entry.getValue(), entry.getKey().getRange(), artists, LocalDate.now());
        }).toList();
        artistRepository.saveAll(userArtistList);
    }

    @Override
    public void saveUserGenres(Map<GenresDTO, Long> dtoMap) {
        List<UserGenre> userGenreList = dtoMap.entrySet().stream().map(entry -> {
            Map<String, Integer> genres = entry.getKey()
                    .getGenres()
                    .stream()
                    .collect(Collectors
                            .toMap(Genre::getGenre, Genre::getScore));
            return new UserGenre(0, entry.getValue(), entry.getKey().getRange(), genres, LocalDate.now());
        }).toList();
        genreRepository.saveAll(userGenreList);
    }

    @Override
    public void saveUserStats(long userId, Map<String, Double> statsMap) {
        User user = userRepository.findById(userId)
                .map(foundUser -> {
                    foundUser.setUserStats(new UserStats(
                            foundUser.getUserStats().getId(),
                            statsMap.get("energy"),
                            statsMap.get("tempo"),
                            statsMap.get("mainstream"),
                            statsMap.get("boringness"),
                            foundUser.getUserStats().getPoints()
                    ));
                    return foundUser;
                }).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.save(user);
    }

    @Override
    public TracksDTO compareTracks(long userId, TracksDTO dto) {
        return trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange())
                .map(item -> {
                    dto.withDate(item.getDate());
                    IntStream.range(0, dto.getTracks().size()).forEach(index -> {
                        try { //handled npe
                            Track track = dto.getTracks().get(index);
                            String artist = track.getArtists().get(0).getName();
                            String name = track.getName();
                            int actualPosition = index + 1;
                            Integer lastPosition = item.getTracks().getOrDefault(artist + "_" + name, null);
                            Integer difference = lastPosition != null ? (lastPosition - actualPosition) : null;
                            track.setDifference(difference);
                            //log.info("Today: " + name + " - " + actualPosition + " / Yesterday: " + name + " - " + lastPosition + " / diff: " + track.getDifference());
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            log.error("Error occurred because there are no artists.", e);
                        }
                    });
                    return dto;
                }).orElse(dto);
    }

    @Override
    public ArtistsDTO compareArtists(long userId, ArtistsDTO dto) {
        return artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange())
                .map(item -> {
                    dto.withDate(item.getDate());
                    IntStream.range(0, dto.getArtists().size()).forEach(index -> {
                        Artist artist = dto.getArtists().get(index);
                        String name = artist.getName();
                        int actualPosition = index + 1;
                        Integer lastPosition = item.getArtists().getOrDefault(name, null);
                        Integer difference = lastPosition != null ? (lastPosition - actualPosition) : null;
                        artist.setDifference(difference);
                        //log.info("Today: " + name + " - " + actualPosition + " / Yesterday: " + name + " - " + lastPosition + " / diff: " + artist.getDifference());
                    });
                return dto;
                }).orElse(dto);
    }

    @Override
    public GenresDTO compareGenres(long userId, GenresDTO dto) {
        return genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange())
                .map(item -> {
                    dto.withDate(item.getDate());
                    IntStream.range(0, dto.getGenres().size()).forEach(index -> {
                        Genre genre = dto.getGenres().get(index);
                        String name = genre.getGenre();
                        int actualScore = genre.getScore();
                        Integer lastScore = item.getGenres().getOrDefault(name, null);
                        Integer difference = lastScore != null ? (actualScore - lastScore) : null;
                        genre.setDifference(difference);
                        //log.info("Today: " + name + " - " + actualScore + " / Yesterday: " + name + " - " + lastScore + " / diff: " + genre.getDifference());
                    });
                    return dto;
                }).orElse(dto);
    }

    @Override
    public Pair<Integer, Integer> matchTracks(long userId, long matchUserId) {
        AtomicInteger comparingSize = new AtomicInteger();
        int matching = Optional.of(trackRepository.findAllByUserId(userId))
                .map(allTracks -> allTracks.stream()
                        .flatMap(userTrack -> userTrack.getTracks().keySet().stream())
                        .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(HashSet::new),
                                collectedTracks -> {
                                    comparingSize.set(collectedTracks.size());
                                    collectedTracks.retainAll(Optional.of(trackRepository.findAllByUserId(matchUserId))
                                            .map(allMatchTracks -> allMatchTracks.stream()
                                                    .flatMap(matchUserTrack -> matchUserTrack.getTracks().keySet().stream())
                                                    .collect(Collectors.toCollection(HashSet::new))
                                            ).orElse(HashSet.newHashSet(0)));
                                    return collectedTracks.size();
                                }
                        ))).orElse(0);
        return Pair.of(matching, comparingSize.get());
    }
}
