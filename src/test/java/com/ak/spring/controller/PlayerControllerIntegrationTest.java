package com.ak.spring.controller;

import java.time.LocalDate;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PlayerController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerControllerIntegrationTest {
  @Autowired
  private TestRestTemplate template;

  @Test
  void index() {
    int size = template.getForObject("/controller/players/", Player[].class).length;
    PlayerController.PlayerRecord playerRecord = new PlayerController.PlayerRecord(
        "Alexander", "V", "K", LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player player = template.postForObject("/controller/players/", playerRecord, Player.class);
    checkEquals(player, playerRecord);
    assertThat(template.getForObject("/controller/players/", Player[].class)).hasSize(size + 1);

    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord(
        "Alexander", "V2", "K2", LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    template.put("/controller/players/%s".formatted(player.getUUID()), playerRecord2);
    Player player2 = template.getForObject("/controller/players/%s".formatted(player.getUUID()), Player.class);
    checkEquals(player2, playerRecord2);
    assertThat(player2).isNotEqualTo(player);
    assertThat(template.getForObject("/controller/players/", Player[].class)).hasSize(size + 1);

    template.delete("/controller/players/%s".formatted(player.getUUID()));
    Player[] history = template.getForObject("/controller/players/history/%s".formatted(player.getUUID()), Player[].class);
    assertThat(history).hasSize(3);
    for (int i = 1; i < history.length; i++) {
      assertThat(history[i].getRevision()).isLessThan(history[i - 1].getRevision());
    }
    assertThat(template.getForObject("/controller/players/", Player[].class)).hasSize(size);
  }

  void checkEquals(@NonNull Player player, @NonNull PlayerController.PlayerRecord playerRecord) {
    assertAll(player.toString(), () -> {
      assertThat(player.getFirstName()).isEqualTo(playerRecord.firstName());
      assertThat(player.getSurName()).isEqualTo(playerRecord.surName());
      assertThat(player.getLastName()).isEqualTo(playerRecord.lastName());
      assertThat(player.getBirthDate()).isEqualTo(playerRecord.birthDate());
      assertThat(player.getGender()).isEqualTo(playerRecord.gender());
    });
  }
}
