package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.Image;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("images")
    private List<Image> images;

    public UserIdDTO() {

    }

    public UserIdDTO(String id, String displayName, List<Image> images) {
        this.id = id;
        this.displayName = displayName;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "UserIdDTO{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", images=" + images +
                '}';
    }
}
