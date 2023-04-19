package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
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
        return null;
    }

    @Override
    public TopArtistsDTO compareArtists(long userId, TopArtistsDTO dto) {
        return null;
    }

    @Override
    public TopGenresDTO compareGenres(long userId, TopGenresDTO dto) {
        return null;
    }

    @Override
    public MainstreamScoreDTO compareMainstream(long userId, MainstreamScoreDTO dto) {
        return null;
    }
}
