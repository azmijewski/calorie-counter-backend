package com.sda.caloriecounterbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class CustomMailSender {
    private final JavaMailSender mailSender;

    public CustomMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String sendTo, String topic, String content) {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage);
        try {
            helper.setTo(sendTo);
            helper.setSubject(topic);
            helper.setText(content);
        } catch (MessagingException e) {
            log.error("Could not send mail: {}", e.getMessage());
        }
        mailSender.send(mimeMailMessage);
        log.info("Mail sent successfully to {}", sendTo);
    }
}
