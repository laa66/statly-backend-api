package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemTopTracks;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({"items", "total"})
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TopTracksDTO {

    @JsonProperty("items")
    private List<ItemTopTracks> itemTopTracks;

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
