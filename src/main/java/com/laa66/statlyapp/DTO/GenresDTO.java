package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.spotify.Genre;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class GenresDTO {

    private List<Genre> genres;
    private String range;
    private LocalDate date;
    private LocalDate lastVisit;

    public GenresDTO withRange(String range) {
        this.range = range;
        return this;
    }

    public GenresDTO withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GenresDTO withLastVisit(LocalDate lastVisit) {
        this.lastVisit = lastVisit;
        return this;
    }
}
