package com.laa66.statlyapp.model.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemTopArtists;

import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyResponseTopArtists {

    @JsonProperty("total")
    private String total;

    @JsonProperty("items")
    private List<ItemTopArtists> itemTopArtists;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ItemTopArtists> getItemTopArtists() {
        return itemTopArtists;
    }

    public void setItemTopArtists(List<ItemTopArtists> itemTopArtists) {
        this.itemTopArtists = itemTopArtists;
    }

    @Override
    public String toString() {
        return "SpotifyResponseTopArtists{" +
                "total='" + total + '\'' +
                ", itemTopArtists=" + itemTopArtists +
                '}';
    }
}
