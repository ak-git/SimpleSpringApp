package com.ak.spring.controller;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PersonController.class, SpringSecurityConfig.class, PersonDetailsService.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PersonControllerIntegrationTest extends AbstractControllerIntegrationTest {
  @Test
  void index() {
    int size = withAuth("admin").getForObject("/controller/persons/", Person[].class).length;
    assertThat(size).isPositive();
    Person person1 = withAuth("admin").postForObject("/controller/persons/", "ak1", Person.class);
    checkEquals(person1, "ak1");
    Person person2 = withAuth("admin").postForObject("/controller/persons/", "ak2", Person.class);
    checkEquals(person2, "ak2");
    assertThat(person1).isNotEqualTo(person2);

    Person person3 = withAuth("admin").getForObject("/controller/persons/%s".formatted("ak1"), Person.class);
    assertThat(person3).isEqualTo(person1);
    withAuth("admin").put("/controller/persons/%s".formatted("ak1"), "password");
    Person person = withAuth("ak1").getForObject("/controller/persons/%s".formatted("ak1"), Person.class);
    checkEquals(person, "ak1");
    assertThat(person).isNotEqualTo(person3).isNotEqualTo(person1);

    withAuth("admin").delete("/controller/persons/%s".formatted("ak1"));
    withAuth("admin").delete("/controller/persons/%s".formatted("ak2"));
    assertThat(withAuth("admin").getForObject("/controller/persons/", Person[].class)).hasSize(size);
  }

  private void checkEquals(@NonNull Person person, @NonNull String name) {
    assertAll(person.toString(), () -> {
      assertThat(person.getName()).isEqualTo(name);
      assertThat(person.getRole()).isEqualTo(Person.Role.USER);
    });
  }
}
