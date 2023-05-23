package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@JsonPropertyOrder({"album", "artists", "name", "popularity", "uri"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTopTracks {

    private Album album;

    private List<Artist> artists;

    private String name;

    private int popularity;

    private String uri;

    @JsonProperty("external_urls")
    private SpotifyURL url;

    private Integer difference;
}
