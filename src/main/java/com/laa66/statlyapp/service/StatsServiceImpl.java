package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.UserArtist;
import com.laa66.statlyapp.entity.UserGenre;
import com.laa66.statlyapp.entity.UserTrack;
import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
import com.laa66.statlyapp.repository.ArtistRepository;
import com.laa66.statlyapp.repository.GenreRepository;
import com.laa66.statlyapp.repository.MainstreamRepository;
import com.laa66.statlyapp.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private final MainstreamRepository mainstreamRepository;

    @Override
    public void saveUserTracks(Map<TopTracksDTO, Long> dtoMap) {
        List<UserTrack> userTrackList = dtoMap.entrySet().stream().map(entry -> {
            AtomicInteger counter = new AtomicInteger(1);
            Map<String, Integer> tracks = entry.getKey()
                    .getItemTopTracks()
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
    public void saveUserArtists(Map<TopArtistsDTO, Long> dtoMap) {
        List<UserArtist> userArtistList = dtoMap.entrySet().stream().map(entry -> {
           AtomicInteger counter = new AtomicInteger(1);
           Map<String, Integer> artists = entry.getKey()
                   .getItemTopArtists()
                   .stream()
                   .collect(Collectors
                           .toMap(ItemTopArtists::getName, s -> counter.getAndIncrement()));
           return new UserArtist(0, entry.getValue(), entry.getKey().getRange(), artists, LocalDate.now());
        }).toList();
        artistRepository.saveAll(userArtistList);
    }

    @Override
    public void saveUserGenres(Map<TopGenresDTO, Long> dtoMap) {
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
    public TopTracksDTO compareTracks(long userId, TopTracksDTO dto) {
        return trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange())
                .map(item -> {
                    dto.withDate(item.getDate());
                    IntStream.range(0, dto.getItemTopTracks().size()).forEach(index -> {
                        try { //handled npe
                            ItemTopTracks track = dto.getItemTopTracks().get(index);
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
    public TopArtistsDTO compareArtists(long userId, TopArtistsDTO dto) {
        return artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange())
                .map(item -> {
                    dto.withDate(item.getDate());
                    IntStream.range(0, dto.getItemTopArtists().size()).forEach(index -> {
                        ItemTopArtists artist = dto.getItemTopArtists().get(index);
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
    public TopGenresDTO compareGenres(long userId, TopGenresDTO dto) {
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
}
