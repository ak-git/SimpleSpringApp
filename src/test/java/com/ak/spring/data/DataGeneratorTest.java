package com.ak.spring.data;

import com.ak.spring.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest(classes = {Application.class, DataGenerator.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class DataGeneratorTest {
  @Autowired
  private CommandLineRunner commandLineRunner;

  @Test
  void commandLineRunner() {
    Assertions.assertNotNull(commandLineRunner);
    org.assertj.core.api.Assertions.assertThatNoException().isThrownBy(() -> commandLineRunner.run());
  }
}