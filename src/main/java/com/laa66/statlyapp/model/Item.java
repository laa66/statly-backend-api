package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"track", "played_at"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    @JsonProperty("track")
    private Track track;

    @JsonProperty("played_at")
    private String playedAt;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }

    @Override
    public String toString() {
        return "Item{" +
                "track=" + track +
                ", playedAt='" + playedAt + '\'' +
                '}';
    }
}
