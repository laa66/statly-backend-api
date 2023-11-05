package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.spotify.Artist;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ArtistsDTO {

    private String total;

    @JsonProperty("items")
    private List<Artist> artists;

    private String range;

    private LocalDate date;

    public ArtistsDTO withRange(String range) {
        this.range = range;
        return this;
    }

    public ArtistsDTO withDate(LocalDate date) {
        this.date = date;
        return this;
    }
}
