package com.laa66.statlyapp.model;

public class Genre implements Comparable<Genre> {

    private String genre;
    private Integer score;
    private Integer difference;

    public Genre() {

    }

    public Genre(String genre, Integer score) {
        this.genre = genre;
        this.score = score;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genre='" + genre + '\'' +
                ", score=" + score +
                ", difference=" + difference +
                '}';
    }

    @Override
    public int compareTo(Genre o) {
        return this.score.compareTo(o.score);
    }
}
