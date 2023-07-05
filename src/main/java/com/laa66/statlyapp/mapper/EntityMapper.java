package com.laa66.statlyapp.mapper;

import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.model.Image;

import java.util.List;

public class EntityMapper {
    public static com.laa66.statlyapp.model.User toUserDTO(User user) {
        return new com.laa66.statlyapp.model.User(
                Long.toString(user.getId()),
                null,
                user.getUsername(),
                List.of(new Image(user.getImage(), null, null)));
    }
}
