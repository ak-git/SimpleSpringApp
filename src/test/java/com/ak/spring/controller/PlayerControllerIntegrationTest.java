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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {Application.class, PlayerController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerControllerIntegrationTest {
  @Autowired
  private TestRestTemplate template;

  @Test
  void index() {
    Player player = template.postForObject("/controller/player/",
        new PlayerController.PlayerRecord("Alexander", "V", "K"), Player.class
    );
    assertNotNull(player);
    template.put("/controller/player/%s".formatted(player.getUUID()),
        new PlayerController.PlayerRecord("Alexander", "V2", "K2")
    );
    assertThat(template.getForObject("/controller/player/%s".formatted(player.getUUID()), Player.class))
        .isNotEqualTo(player);
    template.delete("/controller/player/%s".formatted(player.getUUID()));
    Player[] history = template.getForObject("/controller/player/history/%s".formatted(player.getUUID()), Player[].class);
    assertThat(history).hasSize(3);
    for (int i = 1; i < history.length; i++) {
      assertThat(history[i].getRevision()).isLessThan(history[i - 1].getRevision());
    }
  }
}
