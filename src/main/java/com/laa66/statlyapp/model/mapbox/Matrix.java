package com.laa66.statlyapp.model.mapbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Matrix {

    private String code;

    private List<List<Double>> distances;

    private List<List<Double>> durations;
}
