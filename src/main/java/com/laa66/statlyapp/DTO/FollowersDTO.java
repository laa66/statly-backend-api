package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.User;
import lombok.Value;

import java.util.List;

@Value
public class FollowersDTO {
    long size;
    List<User> users;
}
