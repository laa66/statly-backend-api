package com.laa66.statlyapp.mapper;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.model.Image;

import java.util.List;

public class EntityMapper {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                Long.toString(user.getId()),
                null,
                null,
                user.getUsername(),
                List.of(new Image(user.getImage(), null, null)),
                user.getUserStats().getPoints());
    }
}
