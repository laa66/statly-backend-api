package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Battler;
import lombok.Value;

@Value
public class PlaylistBattleDTO {
    ProfileDTO winnerProfile;
    ProfileDTO loserProfile;
    Battler winner;
    Battler loser;
    double result;
}
