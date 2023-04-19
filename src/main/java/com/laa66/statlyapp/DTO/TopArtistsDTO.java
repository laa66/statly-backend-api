package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemTopArtists;

import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopArtistsDTO {

    @JsonProperty("total")
    private String total;

    @JsonProperty("items")
    private List<ItemTopArtists> itemTopArtists;

    private String range;

    public TopArtistsDTO() {

    }

    public TopArtistsDTO(String total, List<ItemTopArtists> itemTopArtists, String range) {
        this.total = total;
        this.itemTopArtists = itemTopArtists;
        this.range = range;
    }

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

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "TopArtistsDTO{" +
                "total='" + total + '\'' +
                ", itemTopArtists=" + itemTopArtists +
                ", range='" + range + '\'' +
                '}';
    }
}
