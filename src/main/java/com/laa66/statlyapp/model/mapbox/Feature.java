package com.laa66.statlyapp.model.mapbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {

    @JsonProperty("place_type")
    private String[] type;

    private String text;

    private String getFirstType() {
        if (type.length > 0) return type[0];
        return "";
    }
}
