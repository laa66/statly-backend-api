package com.laa66.statlyapp.model.exchange;

public class SpotifyResponseMainstreamScore {
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SpotifyResponseMainstreamScore{" +
                "score=" + score +
                '}';
    }
}
