package com.laa66.statlyapp.model.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SpotifyRequestAddTracks {

    @JsonProperty("uris")
    private List<String> uris;

    @JsonProperty("position")
    private int position;

    public SpotifyRequestAddTracks() {
    }

    public SpotifyRequestAddTracks(List<String> uris, int position) {
        this.uris = uris;
        this.position = position;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SpotifyRequestAddTracks{" +
                "uris=" + uris +
                ", position=" + position +
                '}';
    }
}
