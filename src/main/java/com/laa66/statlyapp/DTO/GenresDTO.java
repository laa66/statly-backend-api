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

    public void withRange(String range) {
        this.range = range;
    }

    public void withDate(LocalDate date) {
        this.date = date;
    }
}
