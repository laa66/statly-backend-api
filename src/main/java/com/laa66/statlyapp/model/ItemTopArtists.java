package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"genres", "images", "name", "url"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemTopArtists {

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("images")
    private List<Image> images;

    @JsonProperty("name")
    private String name;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("external_urls")
    private SpotifyURL url;

    public ItemTopArtists() {

    }

    public ItemTopArtists(List<String> genres, List<Image> images, String name, String uri, SpotifyURL url) {
        this.genres = genres;
        this.images = images;
        this.name = name;
        this.uri = uri;
        this.url = url;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "ItemTopArtists{" +
                "genres=" + genres +
                ", images=" + images +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", url=" + url +
                '}';
    }
}
