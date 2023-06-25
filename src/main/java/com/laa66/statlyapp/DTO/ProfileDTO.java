package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.PlaylistInfo;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.User;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ProfileDTO {
    long id;
    String username;
    String imageUrl;
    int points;
    LocalDateTime joinDate;
    List<User> following;
    List<User> followers;
    List<Track> topTracks;
    List<Artist> topArtists;
    List<PlaylistInfo> userPlaylists;
    double mainstream;
}
