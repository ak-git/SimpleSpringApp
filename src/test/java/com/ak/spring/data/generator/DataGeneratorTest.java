package com.ak.spring.data.generator;

import com.ak.spring.data.repository.PlayerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {DataGenerator.class})
class DataGeneratorTest {
  @MockBean
  private PlayerRepository playerRepository;
  @Autowired
  private CommandLineRunner commandLineRunner;

  @Test
  void commandLineRunner() {
    Assertions.assertNotNull(playerRepository);
    Assertions.assertNotNull(commandLineRunner);
  }
}