package hu.progmasters.blog.service;

import com.itextpdf.text.DocumentException;
import hu.progmasters.blog.config.PdfUtilsConfig;
import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.dto.paypal.CompleteOrder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;


@Component
@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendEmailWithAttachment(CompleteOrder order, Account account, BigDecimal amount) throws DocumentException {
        ByteArrayOutputStream pdfStream = PdfUtilsConfig.generatePdfStreamTwo(order, account, amount);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("blogprogmasters@gmail.com");
            helper.setSubject("Digify Prémium tagság");
            helper.setText("Köszönjük, hogy előfizettél a Prémium tagságra");
            helper.addAttachment("vásárlási bizonylat.pdf", new ByteArrayResource(pdfStream.toByteArray()));
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

}
