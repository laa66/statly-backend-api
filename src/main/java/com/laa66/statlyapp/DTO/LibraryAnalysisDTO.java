package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Image;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class LibraryAnalysisDTO {
    Map<String, Double> libraryAnalysis;
    List<Image> images;
}


