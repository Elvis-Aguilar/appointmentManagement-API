package com.appointment.management.domain.service.auth;

import java.io.File;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTextEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String companyName, String toEmail, String subject, String htmlContent,
                              File... attachments)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        if (!ObjectUtils.isEmpty(attachments)) {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            Stream.of(attachments)
                    .map(FileSystemResource::new)
                    .forEach(file -> {
                        try {
                            messageHelper.addAttachment(file.getFilename(), file);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        message.setFrom(companyName);
        message.setRecipients(MimeMessage.RecipientType.TO, toEmail);
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }
}
