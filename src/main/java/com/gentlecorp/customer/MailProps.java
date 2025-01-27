package com.gentlecorp.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
@Setter
@Getter
@AllArgsConstructor
public class MailProps {
  private String from;
  private String to;
}
