package com.ak.spring.controller;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {Application.class, PlayerController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerControllerIntegrationTest {
  @Autowired
  private TestRestTemplate template;

  @Test
  void index() {
    PlayerController.PlayerRecord record = new PlayerController.PlayerRecord("Alexander", "V", "K");
    Player player = template.postForObject("/controller/player/", record, Player.class);
    assertNotNull(player);
    assertAll(player.toString(), () -> {
      assertThat(player.getUUID().toString()).isNotBlank();
      assertThat(player.getRevision()).isPositive();
      assertThat(player.getFirstName()).isEqualTo(record.firstName());
      assertThat(player.getSurName()).isEqualTo(record.surName());
      assertThat(player.getLastName()).isEqualTo(record.lastName());
    });
  }
}
