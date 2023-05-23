package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;


@JsonPropertyOrder({"images", "name", "genres"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Album {

    private List<Image> images;

    private String name;

    private List<String> genres;
}
