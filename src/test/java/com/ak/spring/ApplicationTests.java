package com.ak.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class ApplicationTests {
  @Autowired
  private CommandLineRunner commandLineRunner;

  @Test
  void contextLoads() {
    Assertions.assertNotNull(commandLineRunner);
  }
}

