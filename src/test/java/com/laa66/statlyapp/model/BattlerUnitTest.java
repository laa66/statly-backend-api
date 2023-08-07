package com.laa66.statlyapp.model;

import com.laa66.statlyapp.DTO.AnalysisDTO;
import com.laa66.statlyapp.model.spotify.Battler;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BattlerUnitTest {

    Battler battler1;
    Battler battler2;

    @Test
    void shouldBattleFirstWin() {
        battler1 = new Battler(1L, new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of())); // 315
        battler2 = new Battler(2L, new AnalysisDTO(
                Map.of("tempo", 110.,
                        "valence", .30,
                        "energy", .55,
                        "danceability", .15,
                        "mainstream", 65.,
                        "boringness", 210.),
                List.of())); // 245
        Pair<Battler, Battler> battle = battler1.battle(battler2);
        assertEquals(battle.getFirst(), battler1);
        assertEquals(battle.getSecond(), battler2);
    }

    @Test
    void shouldBattleSecondWin() {
        battler1 = new Battler(2L, new AnalysisDTO(
                Map.of("tempo", 110.,
                        "valence", .30,
                        "energy", .55,
                        "danceability", .15,
                        "mainstream", 65.,
                        "boringness", 210.),
                List.of())); // 245
        battler2 = new Battler(1L, new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of())); // 315
        Pair<Battler, Battler> battle = battler1.battle(battler2);
        assertEquals(battle.getFirst(), battler2);
        assertEquals(battle.getSecond(), battler1);
    }

    @Test
    void shouldBattleNoContest() {
        battler1 = new Battler(1L, new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of())); // 315
        Pair<Battler, Battler> battle = battler1.battle(battler1);
        assertNull(battle);
    }

}
