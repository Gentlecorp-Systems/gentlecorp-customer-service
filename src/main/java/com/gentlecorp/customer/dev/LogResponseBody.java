package com.gentlecorp.customer.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@Profile("log-body")
@Slf4j
public class LogResponseBody implements ResponseBodyAdvice<Object> {
  @Override
  public boolean supports(
    final MethodParameter returnType,
    final Class<? extends HttpMessageConverter<?>> converterType
  ) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
    final Object body,
    final MethodParameter returnType,
    final MediaType selectedContentType,
    final Class<? extends HttpMessageConverter<?>> selectedConverterType,
    final ServerHttpRequest request,
    final ServerHttpResponse response
  ) {
    log.trace("{}", body);
    return body;
  }
}
