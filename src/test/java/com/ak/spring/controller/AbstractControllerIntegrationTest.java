package com.ak.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

abstract class AbstractControllerIntegrationTest {
  @Autowired
  private TestRestTemplate template;
  @Autowired
  private CsrfTokenRepository csrfTokenRepository;

  TestRestTemplate withAuth(@NonNull String user) {
    CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
    TestRestTemplate testRestTemplate = template.withBasicAuth(user, "password");
    testRestTemplate.getRestTemplate().setInterceptors(List.of(
        (request, body, execution) -> {
          request.getHeaders().add(csrfToken.getHeaderName(), csrfToken.getToken());
          request.getHeaders().add("Cookie", "XSRF-TOKEN=" + csrfToken.getToken());
          return execution.execute(request, body);
        }
    ));
    return testRestTemplate;
  }
}
