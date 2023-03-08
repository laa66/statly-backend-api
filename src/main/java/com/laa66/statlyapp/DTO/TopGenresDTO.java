package com.laa66.statlyapp.DTO;

import java.util.Map;

public class TopGenresDTO {

    private Map<String, Integer> genres;

    public TopGenresDTO() {

    }

    public TopGenresDTO(Map<String, Integer> genres) {
        this.genres = genres;
    }

    public Map<String, Integer> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, Integer> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "TopGenresDTO{" +
                "genres=" + genres +
                '}';
    }
}
