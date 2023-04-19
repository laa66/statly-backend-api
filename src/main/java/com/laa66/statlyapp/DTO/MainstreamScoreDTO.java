package com.laa66.statlyapp.DTO;

public class MainstreamScoreDTO {
    private double score;

    private String range;

    public MainstreamScoreDTO() {

    }

    public MainstreamScoreDTO(double score, String range) {
        this.score = score;
        this.range = range;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "MainstreamScoreDTO{" +
                "score=" + score +
                ", range='" + range + '\'' +
                '}';
    }
}
