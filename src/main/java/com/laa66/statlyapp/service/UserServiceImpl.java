package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void deleteUser(long id) {

    }

    @Override
    public void saveUserTracks(String email, String range, TopTracksDTO dto) {
        System.out.println("Saving user tracks..." + email + " " + range + " " + (dto != null));
    }

    @Override
    public void saveUserArtists(String email, String range, TopArtistsDTO dto) {
        System.out.println("Saving user artists..." + email + " " + range + " " + (dto != null));
    }

    @Override
    public void saveUserGenres(String email, String range, TopGenresDTO dto) {
        System.out.println("Saving user genres..." + email + " " + range + " " + (dto != null));
    }

    @Override
    public void saveUserMainstream(String email, String range, MainstreamScoreDTO dto) {
        System.out.println("Saving user score..." + email + " " + range + " " + (dto != null));
    }

    @Override
    public TopTracksDTO compareTracks(String email, String range, TopTracksDTO dto) {
        return null;
    }

    @Override
    public TopArtistsDTO compareArtists(String email, String range, TopArtistsDTO dto) {
        return null;
    }

    @Override
    public TopGenresDTO compareGenres(String email, String range, TopGenresDTO dto) {
        return null;
    }

    @Override
    public MainstreamScoreDTO compareMainstream(String email, MainstreamScoreDTO dto) {
        return null;
    }
}
