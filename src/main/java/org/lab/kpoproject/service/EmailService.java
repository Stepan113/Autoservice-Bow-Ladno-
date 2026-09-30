package org.lab.kpoproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final String TEMPLATE_PATH =
            "templates/email/new-admin.html";

    private final JavaMailSender mailSender;
    private final String sender;

    public EmailService(
            final JavaMailSender mailSender,
            @Value("${spring.mail.username}") final String sender) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    public void sendNewAdminCredentials(
            final String email,
            final String fio,
            final String password) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            false,
                            StandardCharsets.UTF_8.name());

            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject(
                    "Доступ администратора — K-pop Project");
            helper.setText(createAdminEmail(fio, email, password), true);

            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new MailSendException(
                    "Не удалось отправить письмо " +
                            "новому администратору",
                    e);
        }
    }

    private String createAdminEmail(
            final String fio,
            final String email,
            final String password) throws IOException {
        final ClassPathResource resource =
                new ClassPathResource(TEMPLATE_PATH);
        final String template = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);

        return template
                .replace("{{fio}}", HtmlUtils.htmlEscape(fio))
                .replace("{{email}}", HtmlUtils.htmlEscape(email))
                .replace("{{password}}", HtmlUtils.htmlEscape(password));
    }
}
