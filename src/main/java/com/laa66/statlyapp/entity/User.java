package com.laa66.statlyapp.entity;

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
    @JoinColumn(name = "id")
    List<UserTrack> tracks;

    public User() {

    }

    public User(long id, String email, LocalDateTime joinDate, List<UserTrack> tracks) {
        this.id = id;
        this.email = email;
        this.joinDate = joinDate;
        this.tracks = tracks;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", tracks=" + tracks +
                '}';
    }
}
