package com.laa66.statlyapp.model.spotify;

import com.laa66.statlyapp.DTO.AnalysisDTO;
import lombok.Value;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;

import java.util.Map;

@Value
public class Battler {
    long id;
    double score;
    Map<String, Double> battlerAnalysis;

    public Battler(long id, AnalysisDTO playlistAnalysis) {
        this.id = id;
        this.score = (100. - playlistAnalysis
                .getAnalysis()
                .getOrDefault("mainstream", 0.)) +
                playlistAnalysis
                        .getAnalysis()
                        .getOrDefault("boringness", 0.);
        this.battlerAnalysis = playlistAnalysis.getAnalysis();
    }

    /**
     * @return Pair of Battler objects - winner is first, loser second
     */
    public Pair<Battler, Battler> battle(@NonNull Battler o) {
        int compare = Double.compare(this.score, o.score);
        if (compare < 0)
            return Pair.of(o, this);
        else if (compare > 0)
            return Pair.of(this, o);
        return null;
    }

    public double getDifference(@NonNull Battler o) {
        return Math.abs(this.score - o.score);
    }
}
