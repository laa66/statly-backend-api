package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyResponseRecentlyPlayed {

    @JsonProperty("total")
    private String total;

    @JsonProperty("items")
    private List<ItemRecentlyPlayed> itemRecentlyPlayeds;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ItemRecentlyPlayed> getItems() {
        return itemRecentlyPlayeds;
    }

    public void setItems(List<ItemRecentlyPlayed> itemRecentlyPlayeds) {
        this.itemRecentlyPlayeds = itemRecentlyPlayeds;
    }

    @Override
    public String toString() {
        return "SpotifyResponseRecentlyPlayed{" +
                "total='" + total + '\'' +
                ", itemRecentlyPlayeds=" + itemRecentlyPlayeds +
                '}';
    }
}
