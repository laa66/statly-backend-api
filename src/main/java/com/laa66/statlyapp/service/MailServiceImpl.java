package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final String email;

    public MailServiceImpl(JavaMailSender mailSender, @Value("${statly.api.admin-email}") String email) {
        this.mailSender = mailSender;
        this.email = email;
    }

    @Override
    public void sendJoinBetaNotification() {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setTo(email);
           message.setSubject("Statly-app: User Joined Beta-tests!");
           message.setText("Hello!\nNew user joined beta-tests. " +
                   "Checkout admin panel and add him to the Spotify web developer panel to grant them access to Statly.");
           mailSender.send(message);
    }

    @Override
    public void sendAccessGrantedNotification(BetaUserDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmail());
        message.setSubject("Welcome to the Statly Beta-Test Program!");
        message.setText("Dear " + dto.getFullName() + ",\n" +
                "\n" +
                "We are delighted to inform you that you have successfully joined the beta-tests for our app with Spotify API integration - Statly. Thank you for your interest in our project!\n" +
                "\n" +
                "As a beta-tester, you will have the opportunity to try out the latest features of Statly before they are released to the public, and provide valuable feedback that will help us improve the app for everyone." +
                "To get started, please log in the Statly app.\n" +
                "\n" +
                "Thank you again for joining the beta-tests. We look forward to hearing your feedback and working together to make Statly the best it can be!\n" +
                "\n" +
                "Best regards,\n" +
                "Statly");
        try {
            mailSender.send(message);
            log.info("Notification email sent to: " + dto.getEmail());
        } catch (Exception e) {
            log.warn("Error while sending notification email.");
        }
    }
}
