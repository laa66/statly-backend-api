package com.laa66.statlyapp.model.exchange;

import java.util.Map;

public class SpotifyResponseTopGenres {

    private Map<String, Integer> genres;

    public Map<String, Integer> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, Integer> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "SpotifyResponseTopGenres{" +
                "genres=" + genres +
                '}';
    }
}
