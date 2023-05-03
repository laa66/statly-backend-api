package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    User saveUser(User user);

    void deleteUser(long id);

    void saveBetaUser(BetaUserDTO dto);

    List<BetaUserDTO> findAllBetaUsers();

    void saveUserTracks(Map<TopTracksDTO, Long> dtoMap);

    void saveUserArtists(Map<TopArtistsDTO, Long> dtoMap);

    void saveUserGenres(Map<TopGenresDTO, Long> dtoMap);

    void saveUserMainstream(Map<MainstreamScoreDTO, Long> dtoMap);

    TopTracksDTO compareTracks(long userId, TopTracksDTO dto);

    TopArtistsDTO compareArtists(long userId, TopArtistsDTO dto);

    TopGenresDTO compareGenres(long userId, TopGenresDTO dto);

    MainstreamScoreDTO compareMainstream(long userId, MainstreamScoreDTO dto);

}
