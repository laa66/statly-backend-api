package com.laa66.statlyapp.model;

import lombok.*;

@Data
public class Genre implements Comparable<Genre> {

    private String genre;
    private Integer score;
    private Integer difference;

    public Genre(String genre, Integer score) {
        this.genre = genre;
        this.score = score;
    }

    @Override
    public int compareTo(Genre o) {
        return this.score.compareTo(o.score);
    }
}
