package com.laa66.statlyapp.model.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.DTO.UserDTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistInfo {

    @JsonProperty("external_urls")
    private SpotifyURL urls;

    @NotNull
    private String id;

    private List<Image> images;

    @NotNull
    private String name;

    @JsonProperty("owner")
    private UserDTO user;

}
