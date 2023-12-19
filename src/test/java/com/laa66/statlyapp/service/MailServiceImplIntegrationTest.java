package com.laa66.statlyapp.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.repository.MySQLBaseContainerTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MailServiceImplIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    MailService mailService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(true);

    @Test
    void shouldSendJoinBetaNotification() throws MessagingException, IOException {
        mailService.sendJoinBetaNotification("name", "email", "number ");
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertNotNull(message);
        assertEquals("admin@mail.com", message.getAllRecipients()[0].toString());
        assertEquals("Statly-app: User Joined Beta-tests!", message.getSubject());
        assertNotNull(message.getContent());
    }

    @Test
    void shouldSendAccessGrantedNotification() throws MessagingException, IOException {
        BetaUserDTO dto = new BetaUserDTO("name", "email", "date", false);
        mailService.sendAccessGrantedNotification(dto);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertNotNull(message);
        assertEquals(dto.getEmail(), message.getAllRecipients()[0].toString());
        assertEquals("Welcome to the Statly Beta-Test Program!", message.getSubject());
        assertNotNull(message.getContent());
    }
}
