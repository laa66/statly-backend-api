package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.spotify.Battler;
import lombok.Value;

@Value
public class BattleResultDTO {
    ProfileDTO winnerProfile;
    ProfileDTO loserProfile;
    Battler winner;
    Battler loser;
    double result;
}
