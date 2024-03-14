package com.laa66.statlyapp.util;


import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserInfo;
import com.laa66.statlyapp.entity.UserStats;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMapperUnitTest {

    @Test
    void shouldMapUserToUserDTO() {
        User user = new User(1L, "eId",
                "username",
                "email",
                "imageUrl",
                LocalDateTime.now(),
                new UserStats(1L, 0.7, 0.7, 0.7, 0.7, 50, 1),
                new UserInfo(1L, "", "", "", 54.23, -43.21));

        UserDTO userDTO = EntityMapper.toUserDTO(user);
        assertAll(
                () -> assertEquals("1", userDTO.getId()),
                () -> assertEquals(user.getEmail(), userDTO.getEmail()),
                () -> assertEquals(user.getUsername(), userDTO.getName()),
                () -> assertEquals(user.getImage(), userDTO.getImages().get(0).getUrl()),
                () -> assertEquals(user.getUserStats().getPoints(), userDTO.getPoints()),
                () -> assertEquals(user.getUserInfo().getLongitude(), userDTO.getCoordinates().getLongitude()),
                () -> assertEquals(user.getUserInfo().getLatitude(), userDTO.getCoordinates().getLatitude())
        );
    }

    @Test
    void shouldMapBetaUserToBetaUserDTO() {
        BetaUser betaUser = new BetaUser(
                1L,
                "name",
                "email",
                LocalDateTime.of(2024, 11, 15, 12, 0),
                true
                );

        BetaUserDTO betaUserDTO = EntityMapper.toBetaUserDTO(betaUser);
        assertAll(
                () -> assertEquals(betaUser.getFullName(), betaUserDTO.getFullName()),
                () -> assertEquals(betaUser.getEmail(), betaUserDTO.getEmail()),
                () -> assertEquals(betaUser.getDate().toString(), betaUserDTO.getDate()),
                () -> assertEquals(betaUser.isActive(), betaUserDTO.isActive())
        );
    }
}
