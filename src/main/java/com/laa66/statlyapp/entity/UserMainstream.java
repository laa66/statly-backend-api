package com.laa66.statlyapp.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_mainstream")
public class UserMainstream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time_range")
    private String range;

    private LocalDate date;

    private double score;

    public UserMainstream() {
    }

    public UserMainstream(long id, long userId, String range, LocalDate date, double score) {
        this.id = id;
        this.userId = userId;
        this.range = range;
        this.date = date;
        this.score = score;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserMainstream{" +
                "id=" + id +
                ", userId=" + userId +
                ", range='" + range + '\'' +
                ", date=" + date +
                ", score=" + score +
                '}';
    }
}
