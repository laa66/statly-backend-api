package com.laa66.statlyapp.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.HashMap;


@Entity
@Table(name = "user_tracks")
public class UserTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time_range")
    private String range;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private HashMap<String, Integer> tracks;

    private LocalDate date;

    public UserTrack() {
    }

    public UserTrack(long id, long userId, String range, HashMap<String, Integer> tracks, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.range = range;
        this.tracks = tracks;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public HashMap<String, Integer> getTracks() {
        return tracks;
    }

    public void setTracks(HashMap<String, Integer> tracks) {
        this.tracks = tracks;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserTrack{" +
                "id=" + id +
                ", userId=" + userId +
                ", range='" + range + '\'' +
                ", tracks=" + tracks +
                ", date=" + date +
                '}';
    }
}
