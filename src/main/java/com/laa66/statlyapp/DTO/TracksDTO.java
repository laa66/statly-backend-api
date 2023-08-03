package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.Track;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TracksDTO {

    @JsonProperty("items")
    private List<Track> tracks;

    private String total;

    private String range;

    private LocalDate date;

    public void withRange(String range) {
        this.range = range;
    }

    public void withDate(LocalDate date) {
        this.date = date;
    }
}
