package com.laa66.statlyapp.model.spotify.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.spotify.TrackAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTracksAnalysis {

    @JsonProperty("audio_features")
    private List<TrackAnalysis> tracksAnalysis;

    public void addAll(List<TrackAnalysis> tracksAnalysis) {
        this.tracksAnalysis.addAll(tracksAnalysis);
    }
}
