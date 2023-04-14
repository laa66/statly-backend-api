package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
import com.laa66.statlyapp.repository.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final MainstreamRepository mainstreamRepository;

    public UserServiceImpl(UserRepository userRepository, TrackRepository trackRepository,
                           ArtistRepository artistRepository, GenreRepository genreRepository,
                           MainstreamRepository mainstreamRepository) {
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.mainstreamRepository = mainstreamRepository;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.findById(id).ifPresentOrElse(item -> userRepository.deleteById(item.getId()), () -> {
                    throw new UserNotFoundException("User not found");
                });
    }

    @Override
    public void saveUserTracks(Map<TopTracksDTO, Long> dtoMap) {
        List<UserTrack> list = new ArrayList<>();
        dtoMap.forEach((key, value) -> {
            AtomicInteger counter = new AtomicInteger(1);
            Map<String, Integer> tracks = key.getItemTopTracks().stream()
                    .collect(Collectors
                            .toMap(item -> item.getArtists().get(0).getName() + "_" + item.getName(), s -> counter.getAndIncrement()));
            UserTrack userTrack = new UserTrack(0, value, key.getRange(), tracks, LocalDate.now());
            list.add(userTrack);
        });
        trackRepository.saveAll(list);
    }

    @Override
    public void saveUserArtists(Map<TopArtistsDTO, Long> dtoMap) {
        List<UserArtist> list = new ArrayList<>();
        dtoMap.forEach((key, value) -> {
            AtomicInteger counter = new AtomicInteger(1);
            Map<String, Integer> artists = key.getItemTopArtists().stream()
                            .collect(Collectors.toMap(ItemTopArtists::getName, s -> counter.getAndIncrement()));
            UserArtist userArtist = new UserArtist(0, value, key.getRange(), artists, LocalDate.now());
            list.add(userArtist);
        });
        artistRepository.saveAll(list);
    }

    @Override
    public void saveUserGenres(Map<TopGenresDTO, Long> dtoMap) {
        List<UserGenre> list = new ArrayList<>();
        dtoMap.forEach((key, value) -> {
            Map<String, Integer> genres = key.getGenres().stream()
                    .collect(Collectors.toMap(Genre::getGenre, Genre::getScore));
            UserGenre userGenre = new UserGenre(0, value, key.getRange(), genres, LocalDate.now());
            list.add(userGenre);
        });
        genreRepository.saveAll(list);
    }

    @Override
    public void saveUserMainstream(Map<MainstreamScoreDTO, Long> dtoMap) {
        List<UserMainstream> list = new ArrayList<>();
        dtoMap.forEach((key, value) -> {
            UserMainstream userMainstream = new UserMainstream(0, value, key.getRange(), LocalDate.now(), key.getScore());
            list.add(userMainstream);
        });
        mainstreamRepository.saveAll(list);
    }

    @Override
    public TopTracksDTO compareTracks(long userId, TopTracksDTO dto) {
        Optional<UserTrack> userTrack = trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange());
        userTrack.ifPresent(item -> {
            IntStream.range(0, dto.getItemTopTracks().size()).forEach(index -> {
                ItemTopTracks track = dto.getItemTopTracks().get(index);
                String artist = track.getArtists().get(0).getName();
                String name = track.getName();
                int actualPosition = index + 1;
                int lastPosition = item.getTracks().getOrDefault(artist + "_" + name, dto.getItemTopTracks().size() + 1);
                track.setDifference(lastPosition - actualPosition);
                //System.out.println("Today: " + name + " - " + actualPosition + " / Yesterday: " + name + " - " + lastPosition + " / diff: " + track.getDifference());
            });
        });
        return dto;
    }

    @Override
    public TopArtistsDTO compareArtists(long userId, TopArtistsDTO dto) {
        Optional<UserArtist> userArtist = artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange());
        userArtist.ifPresent(item -> {
            IntStream.range(0, dto.getItemTopArtists().size()).forEach(index -> {
                ItemTopArtists artist = dto.getItemTopArtists().get(index);
                String name = artist.getName();
                int actualPosition = index + 1;
                int lastPosition = item.getArtists().getOrDefault(name, dto.getItemTopArtists().size() + 1);
                artist.setDifference(lastPosition - actualPosition);
                //System.out.println("Today: " + name + " - " + actualPosition + " / Yesterday: " + name + " - " + lastPosition + " / diff: " + artist.getDifference());
            });
        });
        return dto;
    }

    @Override
    public TopGenresDTO compareGenres(long userId, TopGenresDTO dto) {
        Optional<UserGenre> userGenre = genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange());
        userGenre.ifPresent(item -> {
            IntStream.range(0, dto.getGenres().size()).forEach(index -> {
                Genre genre = dto.getGenres().get(index);
                String name = genre.getGenre();
                int actualScore = genre.getScore();
                int lastScore = item.getGenres().getOrDefault(name, 0);
                genre.setDifference(actualScore - lastScore);
                //System.out.println("Today: " + name + " - " + actualScore + " / Yesterday: " + name + " - " + lastScore + " / diff: " + genre.getDifference());
            });
        });
        return dto;
    }

    @Override
    public MainstreamScoreDTO compareMainstream(long userId, MainstreamScoreDTO dto) {
        Optional<UserMainstream> userMainstream = mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(userId, dto.getRange());
        userMainstream.ifPresent(item -> {
            double actualScore = dto.getScore();
            double lastScore = userMainstream.get().getScore();
            dto.setDifference(new BigDecimal(actualScore - lastScore).setScale(2, RoundingMode.HALF_UP).doubleValue());
            //System.out.println("Today: " + actualScore + " / Yesterday: " + lastScore + " / diff: " + dto.getDifference());
        });
        return dto;
    }
}