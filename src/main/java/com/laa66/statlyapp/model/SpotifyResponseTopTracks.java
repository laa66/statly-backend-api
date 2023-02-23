package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"items", "total"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyResponseTopTracks {

    @JsonProperty("items")
    private List<ItemTopTracks> itemTopTracks;

    @JsonProperty("total")
    private String total;

    public List<ItemTopTracks> getItemTopTracks() {
        return itemTopTracks;
    }

    public void setItemTopTracks(List<ItemTopTracks> itemTopTracks) {
        this.itemTopTracks = itemTopTracks;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "SpotifyResponseTopTracks{" +
                "itemTopTracks=" + itemTopTracks +
                ", total='" + total + '\'' +
                '}';
    }
}
