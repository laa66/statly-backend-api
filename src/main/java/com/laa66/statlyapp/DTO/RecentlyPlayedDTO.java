package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.ItemRecentlyPlayed;

import java.util.List;

@JsonPropertyOrder({"total", "items"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecentlyPlayedDTO {

    @JsonProperty("total")
    private String total;

    @JsonProperty("items")
    private List<ItemRecentlyPlayed> itemRecentlyPlayedList;

    public RecentlyPlayedDTO() {
    }

    public RecentlyPlayedDTO(String total, List<ItemRecentlyPlayed> itemRecentlyPlayedList) {
        this.total = total;
        this.itemRecentlyPlayedList = itemRecentlyPlayedList;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ItemRecentlyPlayed> getItemRecentlyPlayedList() {
        return itemRecentlyPlayedList;
    }

    public void setItemRecentlyPlayedList(List<ItemRecentlyPlayed> itemRecentlyPlayedList) {
        this.itemRecentlyPlayedList = itemRecentlyPlayedList;
    }

    @Override
    public String toString() {
        return "RecentlyPlayedDTO{" +
                "total='" + total + '\'' +
                ", itemRecentlyPlayedList=" + itemRecentlyPlayedList +
                '}';
    }
}
