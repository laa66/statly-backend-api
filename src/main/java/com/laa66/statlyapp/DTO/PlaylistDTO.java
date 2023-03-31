package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.SpotifyURL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("external_urls")
    private SpotifyURL url;

    public PlaylistDTO() {
    }

    public PlaylistDTO(String id, SpotifyURL url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SpotifyURL getUrl() {
        return url;
    }

    public void setUrl(SpotifyURL url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PlaylistDTO{" +
                "id='" + id + '\'' +
                ", url=" + url +
                '}';
    }
}
