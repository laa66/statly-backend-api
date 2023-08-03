package com.laa66.statlyapp.model.spotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackAnalysis {

    private double acousticness;

    private double danceability;

    private double energy;

    private double instrumentalness;

    private double liveness;

    private double loudness;

    private double speechiness;

    private double tempo;

    private double valence;

}
