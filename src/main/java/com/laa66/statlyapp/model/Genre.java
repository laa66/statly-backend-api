package com.laa66.statlyapp.model;

public class Genre implements Comparable<Genre> {

    private String genre;
    private Integer score;
    private int difference;

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

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
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
