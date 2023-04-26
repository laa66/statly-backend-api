package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemTopArtists;
import lombok.*;

import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TopArtistsDTO {

    @JsonProperty("total")
    private String total;

    @JsonProperty("items")
    private List<ItemTopArtists> itemTopArtists;

    private String range;
}
