package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.Artist;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TopArtistsDTO {

    private String total;

    @JsonProperty("items")
    private List<Artist> itemTopArtists;

    private String range;

    private LocalDate date;

    public void withRange(String range) {
        this.range = range;
    }

    public void withDate(LocalDate date) {
        this.date = date;
    }
}
