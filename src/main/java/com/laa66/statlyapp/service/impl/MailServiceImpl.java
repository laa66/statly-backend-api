package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.service.MailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final String email;
    private final JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    @Override
    public void sendJoinBetaNotification(String fullName, String email, String phoneNumber) {
        String additionalInfo = "Personal info: "
                + fullName + "\n"
                + "Email address: "
                + email + "\n"
                + "Phone number: "
                + phoneNumber;
        Resource resource = new ClassPathResource("json/join-beta-mail.json");
        try {
            JSONObject object = (JSONObject) parser.parse(resource.getInputStream());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(this.email);
            message.setSubject((String) object.get("subject"));
            message.setText(object.get("text") + "\n\n" + additionalInfo);
            mailSender.send(message);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Failed to read mail template.", e);
        }
    }

    @Override
    public void sendAccessGrantedNotification(BetaUserDTO dto) {
        Resource resource = new ClassPathResource("json/access-granted-mail.json");
        try {
            JSONObject object = (JSONObject) parser.parse(resource.getInputStream());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(dto.getEmail());
            message.setSubject((String) object.get("subject"));
            String text = ((String) object.get("text")).replace("{fullname}", dto.getFullName());
            message.setText(text);
            mailSender.send(message);
        } catch (MailException e) {
            log.warn("Error while sending notification email.");
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Failed to read mail template.", e);
        }
    }
}
