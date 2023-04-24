package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"album", "artists", "name", "popularity", "uri"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemTopTracks {

    @JsonProperty("album")
    private Album album;

    @JsonProperty("artists")
    private List<Artist> artists;

    @JsonProperty("name")
    private String name;

    @JsonProperty("popularity")
    private int popularity;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("external_urls")
    private SpotifyURL url;

    @JsonProperty("difference")
    private int difference;

    public ItemTopTracks() {
    }

    public ItemTopTracks(Album album, List<Artist> artists, String name, int popularity, String uri, SpotifyURL url, int difference) {
        this.album = album;
        this.artists = artists;
        this.name = name;
        this.popularity = popularity;
        this.uri = uri;
        this.url = url;
        this.difference = difference;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

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

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SpotifyURL getUrl() {
        return url;
    }

    public void setUrl(SpotifyURL url) {
        this.url = url;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

    @Override
    public String toString() {
        return "ItemTopTracks{" +
                "album=" + album +
                ", artists=" + artists +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                ", uri='" + uri + '\'' +
                ", url=" + url +
                ", difference=" + difference +
                '}';
    }
}
