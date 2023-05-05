package com.laa66.statlyapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final String email;

    public MailServiceImpl(JavaMailSender mailSender, @Value("${STATLY_ADMIN_EMAIL}") String email) {
        this.mailSender = mailSender;
        this.email = email;
    }

    @Override
    public void sendJoinBetaNotification() {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom("noreply@statly-app.com");
           message.setTo(email);
           message.setSubject("Statly-app: User Joined Beta-tests!");
           message.setText("Hello!\nNew user joined beta-tests. " +
                   "Checkout admin panel and add him to the Spotify web developer panel to grant them access to Statly.");
           mailSender.send(message);
    }
}
