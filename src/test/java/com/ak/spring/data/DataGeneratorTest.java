package com.ak.spring.data;

import com.ak.spring.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest(classes = {Application.class, DataGenerator.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class DataGeneratorTest {
  @Test
  void generatePlayers(@Autowired CommandLineRunner generatePlayers) {
    Assertions.assertNotNull(generatePlayers);
    assertThatNoException().isThrownBy(generatePlayers::run);
  }
}