package com.laa66.statlyapp.DTO;

public class MainstreamScoreDTO {
    private double score;

    public double getScore() {
        return score;
    }

    public MainstreamScoreDTO() {

    }

    public MainstreamScoreDTO(double score) {
        this.score = score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "MainstreamScoreDTO{" +
                "score=" + score +
                '}';
    }
}
