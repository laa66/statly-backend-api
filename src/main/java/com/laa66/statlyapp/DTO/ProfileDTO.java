package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Value
public class ProfileDTO {
    long id;
    String externalId;
    String username;
    String imageUrl;
    LocalDateTime joinDate;
    List<UserDTO> following;
    List<UserDTO> followers;
    List<Track> topTracks;
    List<Artist> topArtists;
    Map<String, Double> statsMap;
    String linkIg;
    String linkFb;
    String linkTwitter;
    long points;
}
