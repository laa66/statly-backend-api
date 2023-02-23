package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"track", "played_at"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemRecentlyPlayed {

    @JsonProperty("track")
    private Track track;

    @JsonProperty("played_at")
    private LocalDateTime playedAt;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    @Override
    public String toString() {
        return "ItemRecentlyPlayed{" +
                "track=" + track +
                ", playedAt='" + playedAt + '\'' +
                '}';
    }
}
