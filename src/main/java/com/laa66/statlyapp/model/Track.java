package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"album", "artists", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Track {

    private Album album;

    private List<Artist> artists;

    private String name;

    @JsonProperty("external_urls")
    private SpotifyURL url;
}
