package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.User;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class UserProfileDTO {
    long id;
    String username;
    String imageUrl;
    int points;
    LocalDateTime joinDate;
    List<User> followed;
    List<User> followers;
    List<Track> topTracks;
    List<Artist> topArtists;
    List<ResponsePlaylists> userPlaylists;
    int mainstream;
}
