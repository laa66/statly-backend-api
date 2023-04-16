package com.laa66.statlyapp.entity;

import com.laa66.statlyapp.model.Artist;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_artists")
public class UserArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time_range")
    private String range;

    private LocalDate date;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private List<Artist> artists;

    public UserArtist() {
    }

    public UserArtist(long id, long userId, String range, LocalDate date, List<Artist> artists) {
        this.id = id;
        this.userId = userId;
        this.range = range;
        this.date = date;
        this.artists = artists;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "UserArtist{" +
                "id=" + id +
                ", userId=" + userId +
                ", range='" + range + '\'' +
                ", date=" + date +
                ", artists=" + artists +
                '}';
    }
}