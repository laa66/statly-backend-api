package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class SpotifyRequestAddTracks {

    private List<String> uris;

    private int position;
}
