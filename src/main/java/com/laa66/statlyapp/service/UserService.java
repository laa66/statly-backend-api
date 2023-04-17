package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    void saveUser(User user);

    void deleteUser(long id);

    void saveUserTracks(String email, String range, TopTracksDTO dto);

    void saveUserArtists(String email, String range, TopArtistsDTO dto);

    void saveUserGenres(String email, String range, TopGenresDTO dto);

    void saveUserMainstream(String email, String range, MainstreamScoreDTO dto);

    TopTracksDTO compareTracks(String email, String range, TopTracksDTO dto);

    TopArtistsDTO compareArtists(String email, String range, TopArtistsDTO dto);

    TopGenresDTO compareGenres(String email, String range, TopGenresDTO dto);

    MainstreamScoreDTO compareMainstream(String email, MainstreamScoreDTO dto);

}
