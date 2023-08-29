package com.laa66.statlyapp.model.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {

    private String name;

    private String next;

    @JsonProperty("items")
    private List<PlaylistTrack> tracks;

    public void addAll(List<PlaylistTrack> playlistTracks) {
        tracks.addAll(playlistTracks);
    }

}
