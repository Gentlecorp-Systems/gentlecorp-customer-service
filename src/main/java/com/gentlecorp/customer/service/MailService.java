package com.gentlecorp.customer.service;

import com.gentlecorp.customer.MailProps;
import com.gentlecorp.customer.model.entity.Customer;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static jakarta.mail.Message.RecipientType.TO;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

  private static final boolean SMTP_ACTIVATED = Objects.equals(System.getenv("SMTP_ACTIVATED"), "true") ||
    Objects.equals(System.getProperty("smtp-activated"), "true");

  private final JavaMailSender mailSender;
  private final MailProps props;

  @Value("${spring.mail.host}")
  private String mailhost;

  @Async
  public void send(final Customer neuerKunde) {
    if (!SMTP_ACTIVATED) {
      log.warn("SMTP is disabled.");
      return;
    }

    final MimeMessagePreparator preparator = mimeMessage -> {
      mimeMessage.setFrom(new InternetAddress(props.getFrom()));
      mimeMessage.setRecipient(TO, new InternetAddress(props.getTo()));
      mimeMessage.setSubject(String.format("New Customer %s", neuerKunde.getId()));
      final var body = String.format("<strong>New Customer:</strong> <em>%s</em>", neuerKunde.getLastName());
      log.trace("send: Mail server={}, Thread ID={}, body={}", mailhost, Thread.currentThread().threadId(), body);
      mimeMessage.setText(body, "UTF-8", "html");
    };

    try {
      mailSender.send(preparator);
    } catch (final MailSendException | MailAuthenticationException e) {
      // TODO: Add retry logic for sending the email
      log.warn("Email not sent: Is the mail server {} reachable?", mailhost);
    }
  }
}
