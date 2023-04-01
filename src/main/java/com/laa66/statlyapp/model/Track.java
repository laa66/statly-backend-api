package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"album", "artists", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

    @JsonProperty("album")
    private Album album;

    @JsonProperty("artists")
    private List<Artist> artists;

    @JsonProperty("name")
    private String name;

    @JsonProperty("external_urls")
    private SpotifyURL url;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public SpotifyURL getUrl() {
        return url;
    }

    public void setUrl(SpotifyURL url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Track{" +
                "album=" + album +
                ", artists=" + artists +
                ", name='" + name + '\'' +
                ", url=" + url +
                '}';
    }
}
