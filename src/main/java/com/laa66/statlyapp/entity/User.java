package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String email;

    @Column(name = "image_url")
    private String image;

    private int points;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id", referencedColumnName = "id")}
    )
    private List<User> following = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "friend_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> followers = new ArrayList<>();

    public User(long id, String username, String email, String image, int points, LocalDateTime joinDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.image = image;
        this.points = points;
        this.joinDate = joinDate;
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

    public void addFollower(User follower) {
        following.add(follower);
    }

    public void removeFollower(User follower) {
        if (following == null) return;
        following.remove(follower);
    }
}
