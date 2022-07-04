package com.ak.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class, HelloController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloControllerIntegrationTest {

  @Autowired
  private TestRestTemplate template;

  @Test
  void index() {
    ResponseEntity<String> response = template.getForEntity("/", String.class);
    assertThat(response.getBody()).startsWith("Greetings from Spring Boot").endsWith("!");
  }

  @Test
  void greeting() {
    Greeting response = template.getForObject("/greeting/", Greeting.class);
    assertThat(response).matches(greeting -> greeting.id() > 0).matches(greeting -> greeting.content().equals("Hello, World!"));
  }
}
