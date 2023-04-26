package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpotifyRequestAddTracks {

    @JsonProperty("uris")
    private List<String> uris;

    @JsonProperty("position")
    private int position;
}
