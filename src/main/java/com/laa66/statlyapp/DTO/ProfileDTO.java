package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.User;
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
    List<User> following;
    List<User> followers;
    List<Track> topTracks;
    List<Artist> topArtists;
    Map<String, Double> statsMap;
    long points;
}
