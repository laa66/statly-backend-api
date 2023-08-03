package com.laa66.statlyapp.model.spotify.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.spotify.PlaylistInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePlaylists {

    private String next;

    private int total;

    @JsonProperty("items")
    private List<PlaylistInfo> playlists;

    public void addAll(List<PlaylistInfo> playlistInfoList) {
        playlists.addAll(playlistInfoList);
    }
}
