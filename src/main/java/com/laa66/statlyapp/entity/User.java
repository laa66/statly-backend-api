package com.laa66.statlyapp.entity;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
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

    public User() {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public List<UserTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<UserTrack> tracks) {
        this.tracks = tracks;
    }

    public List<UserArtist> getArtists() {
        return artists;
    }

    public void setArtists(List<UserArtist> artists) {
        this.artists = artists;
    }

    public List<UserGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<UserGenre> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", tracks=" + tracks +
                ", artists=" + artists +
                ", genres=" + genres +
                '}';
    }
}
