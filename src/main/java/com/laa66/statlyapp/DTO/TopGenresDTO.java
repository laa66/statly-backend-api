package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Genre;

import java.util.List;

public class TopGenresDTO {

    private List<Genre> genres;

    public TopGenresDTO() {

    }

    public TopGenresDTO(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "TopGenresDTO{" +
                "genres=" + genres +
                '}';
    }
}
