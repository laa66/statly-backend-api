package com.laa66.statlyapp.util;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.model.spotify.Image;
import org.springframework.data.util.Pair;

import java.util.List;

public class EntityMapper {
    public static UserDTO toUserDTO(User user) {
        boolean hasLocation = user.getUserInfo().getLatitude() != null && user.getUserInfo().getLongitude() != null;
        return UserDTO.builder()
                .id(Long.toString(user.getId()))
                .email(user.getEmail())
                .name(user.getUsername())
                .images(List.of(new Image(user.getImage(), null, null)))
                .points(user.getUserStats().getPoints())
                .coordinates(hasLocation ? CoordinateEncryptor.decrypt(Pair.of(
                        user.getUserInfo().getLongitude(),
                        user.getUserInfo().getLatitude())) : null)
                .build();
    }

    public static BetaUserDTO toBetaUserDTO(BetaUser betaUser) {
        return new BetaUserDTO(
                betaUser.getFullName(),
                betaUser.getEmail(),
                betaUser.getDate().toString(),
                betaUser.isActive()
        );
    }
}
