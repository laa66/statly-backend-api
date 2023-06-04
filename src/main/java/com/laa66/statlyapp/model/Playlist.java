package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    @JsonProperty("external_urls")
    private SpotifyURL urls;

    private String id;

    private List<Image> images;

    private String name;

    @JsonProperty("owner")
    private User user;

}
