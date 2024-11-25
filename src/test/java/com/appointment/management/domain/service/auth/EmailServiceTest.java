package com.appointment.management.domain.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //Given: sin given global pues no comparten given...
    }

    @Test
    void sendTextEmail_ShouldSendEmail_WhenParametersAreValid() {
        // Given
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // When
        emailService.sendTextEmail(toEmail, subject, body);

        // Then
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        verify(mailSender, times(1)).send(message);
    }

    @Test
    void sendHtmlEmail_ShouldSendEmail_WhenParametersAreValid() throws MessagingException {
        // Given
        String companyName = "Test Company";
        String toEmail = "test@example.com";
        String subject = "Test HTML Subject";
        String htmlContent = "<h1>Test Body</h1>";
        File attachment = new File("path/to/attachment.txt");

        MimeMessage mimeMessage = mock(MimeMessage.class);

        // When
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(htmlContent, true);

        // Then
        emailService.sendHtmlEmail(companyName, toEmail, subject, htmlContent, attachment);
        verify(mailSender, times(1)).send(mimeMessage);
    }


}




