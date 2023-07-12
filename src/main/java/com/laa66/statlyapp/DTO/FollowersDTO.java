package com.laa66.statlyapp.DTO;

import lombok.Value;

import java.util.List;

@Value
public class FollowersDTO {
    long size;
    List<UserDTO> users;
}
