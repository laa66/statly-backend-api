package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.SpotifyURL;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
// TODO: 07.07.2023 merge this with ResponsePlaylists
public class PlaylistDTO {

    String id;

    @JsonProperty("external_urls")
    SpotifyURL url;

}
