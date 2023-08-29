package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.spotify.Image;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AnalysisDTO {
    Map<String, Double> analysis;
    List<Image> images;
}


