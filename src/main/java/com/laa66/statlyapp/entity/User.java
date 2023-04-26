package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserTrack> tracks;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserArtist> artists;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserGenre> genres;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserMainstream> mainstreamList;

    public User(long id, String email, LocalDateTime joinDate) {
        this.id = id;
        this.email = email;
        this.joinDate = joinDate;
    }

    public User(long id, String email, LocalDateTime joinDate, List<UserTrack> tracks, List<UserArtist> artists, List<UserGenre> genres) {
        this.id = id;
        this.email = email;
        this.joinDate = joinDate;
        this.tracks = tracks;
        this.artists = artists;
        this.genres = genres;
    }

    public void addTrack(UserTrack track) {
        if (tracks == null) tracks = new ArrayList<>();
        tracks.add(track);
    }

    public void addArtist(UserArtist artist) {
        if (artists == null) artists = new ArrayList<>();
        artists.add(artist);
    }

    public void addGenre(UserGenre genre) {
        if (genres == null) genres = new ArrayList<>();
        genres.add(genre);
    }

    public void addMainstream(UserMainstream mainstream) {
        if (mainstreamList == null) mainstreamList = new ArrayList<>();
        mainstreamList.add(mainstream);
    }
}
