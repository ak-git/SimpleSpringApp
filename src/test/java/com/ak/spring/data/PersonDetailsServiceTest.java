package com.ak.spring.data;

import java.util.stream.Stream;

import com.ak.spring.Application;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {Application.class, PersonRepository.class, SpringSecurityConfig.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PersonDetailsServiceTest {
  @Autowired
  private PersonRepository repository;
  @Autowired
  private PasswordEncoder encoder;

  @Test
  void generatePersons() {
    assertNotNull(repository);
    assertNotNull(encoder);
    Stream.generate(() -> new PersonDetailsService(repository, encoder)).limit(2)
        .forEach(personDetailsService ->
            assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(
                () -> personDetailsService.loadUserByUsername(Strings.EMPTY)
            )
        );
  }
}