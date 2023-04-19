package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Genre;

import java.util.List;

public class TopGenresDTO {

    private List<Genre> genres;

    private String range;

    public TopGenresDTO() {

    }

    public TopGenresDTO(List<Genre> genres, String range) {
        this.genres = genres;
        this.range = range;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "TopGenresDTO{" +
                "genres=" + genres +
                ", range='" + range + '\'' +
                '}';
    }
}
