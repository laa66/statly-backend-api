package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.PlaylistInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.lang.NonNull;


@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BattleDTO {
    @NonNull
    PlaylistInfo playlist;
    @NonNull
    PlaylistInfo playlistBattle;
}
