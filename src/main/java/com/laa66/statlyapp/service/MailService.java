package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;

public interface MailService {
    void sendJoinBetaNotification();
    void sendAccessGrantedNotification(BetaUserDTO dto);
}
