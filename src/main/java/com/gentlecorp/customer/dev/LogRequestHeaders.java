package com.gentlecorp.customer.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

interface LogRequestHeaders {
  /**
   * Creates a {@link CommonsRequestLoggingFilter} bean for logging request headers and query strings.
   * <p>
   * This method sets up a {@link CommonsRequestLoggingFilter} to include query strings and request headers
   * in the log output. It helps in capturing detailed information about incoming HTTP requests,
   * which can be valuable for debugging and monitoring purposes.
   * </p>
   *
   * @return a {@link CommonsRequestLoggingFilter} configured to log request headers and query strings.
   */
  @Bean
  default CommonsRequestLoggingFilter logFilter() {
    final var filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludeHeaders(true);
    return filter;
  }
}
