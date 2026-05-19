package edu.sabanciuniv.cs308ecommercebackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MailService
{
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@teknocs.com}")
    private String fromAddress;

    /**
     * Sends a multipart email with an HTML body and a single PDF attachment.
     * Throws MessagingException if the SMTP send fails — callers decide whether
     * a mail failure should bubble up or be logged-and-swallowed.
     */
    public void sendInvoiceEmail(
            String to,
            String subject,
            String htmlBody,
            byte[] pdfBytes,
            String pdfFilename
    ) throws MessagingException
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, true, StandardCharsets.UTF_8.name());

        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML
        helper.addAttachment(pdfFilename, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}