package hu.progmasters.blog.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String recipientEmail, String subject, String templateName, Map<String, Object> variables) {
        String bodyHtml = generateEmailContent(templateName, variables);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
        }
    }

    private String generateEmailContent(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }

    public void sendThankYouEmail(String userEmail, String customerName) {
        String subject = "Thank You for Subscribing to Premium!";
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);

        sendEmail(userEmail, subject, "thank-you-email", variables);
    }

    public void sendPasswordResetEmail(String userEmail, String customerName, String resetLink) {
        String subject = "Password Reset Request";
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("resetLink", resetLink);

        sendEmail(userEmail, subject, "password-reset-email", variables);
    }

    public void sendPasswordResetSuccessEmail(String email, String customerName) {
        String subject = "Password Successfully Reset";
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("message", "Your password has been successfully reset.");

        sendEmail(email, subject, "password-reset-success-email", variables);
    }

}
