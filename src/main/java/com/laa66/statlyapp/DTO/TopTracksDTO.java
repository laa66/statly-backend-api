package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemTopTracks;

import java.util.List;

@JsonPropertyOrder({"items", "total"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopTracksDTO {

    @JsonProperty("items")
    private List<ItemTopTracks> itemTopTracks;

    @JsonProperty("total")
    private String total;

    private String range;

    public TopTracksDTO() {
    }

    public TopTracksDTO(List<ItemTopTracks> itemTopTracks, String total, String range) {
        this.itemTopTracks = itemTopTracks;
        this.total = total;
        this.range = range;
    }

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

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "TopTracksDTO{" +
                "itemTopTracks=" + itemTopTracks +
                ", total='" + total + '\'' +
                ", range='" + range + '\'' +
                '}';
    }
}
