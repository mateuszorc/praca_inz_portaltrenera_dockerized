package com.example.portaltrenera.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSender {

    private static final Logger LOGGER = LogManager.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    @Value("${portalTrenera.mail.mailName}")
    private String mail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Potwierdź swój email dla Portalu Trenera");
            helper.setFrom(mail);
            this.mailSender.send(mimeMessage);
            LOGGER.info("Registration email has been sent to: " + email);
        } catch (MessagingException var5) {
            LOGGER.error("An error occured, email couldn't be sent!", var5);
            throw new IllegalStateException("An error occured, email couldn't be sent!");
        }
    }
}
