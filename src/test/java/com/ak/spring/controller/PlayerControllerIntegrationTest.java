package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PlayerController.class, SpringSecurityConfig.class, PersonDetailsService.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerControllerIntegrationTest {
  @Autowired
  private TestRestTemplate template;
  @Autowired
  private CsrfTokenRepository csrfTokenRepository;

  @Test
  void index() {
    int size = withAuth("user").getForObject("/controller/players/", Player[].class).length;
    PlayerController.PlayerRecord playerRecord = new PlayerController.PlayerRecord(
        "Alexander", Strings.EMPTY, Strings.EMPTY, LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player player = withAuth("user").postForObject("/controller/players/", playerRecord, Player.class);
    checkEquals(player, playerRecord);
    assertThat(withAuth("user").getForObject("/controller/players/", Player[].class)).hasSize(size + 1);

    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord(
        "Alexander", "V2", "K2", LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    withAuth("user").put("/controller/players/%s".formatted(player.getUUID()), playerRecord2);
    Player player2 = withAuth("user").getForObject("/controller/players/%s".formatted(player.getUUID()), Player.class);
    checkEquals(player2, playerRecord2);
    assertThat(player2).isNotEqualTo(player);
    assertThat(withAuth("user").getForObject("/controller/players/", Player[].class)).hasSize(size + 1);

    withAuth("user").delete("/controller/players/%s".formatted(player.getUUID()));
    Player[] history = withAuth("admin").getForObject("/controller/players/history/%s".formatted(player.getUUID()), Player[].class);
    assertThat(history).hasSize(3);
    for (int i = 1; i < history.length; i++) {
      assertThat(history[i].getRevision()).isLessThan(history[i - 1].getRevision());
    }
    assertThat(withAuth("user").getForObject("/controller/players/", Player[].class)).hasSize(size);
  }

  private void checkEquals(@NonNull Player player, @NonNull PlayerController.PlayerRecord playerRecord) {
    assertAll(player.toString(), () -> {
      assertThat(player.getFirstName()).isEqualTo(playerRecord.firstName());
      assertThat(player.getSurName()).isEqualTo(playerRecord.surName());
      assertThat(player.getLastName()).isEqualTo(playerRecord.lastName());
      assertThat(player.getBirthDate()).isEqualTo(playerRecord.birthDate());
      assertThat(player.getGender()).isEqualTo(playerRecord.gender());
    });
  }

  private TestRestTemplate withAuth(@NonNull String user) {
    CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
    TestRestTemplate testRestTemplate = template.withBasicAuth(user, "password");
    testRestTemplate.getRestTemplate().setInterceptors(List.of(
        (request, body, execution) -> {
          request.getHeaders().add(csrfToken.getHeaderName(), csrfToken.getToken());
          request.getHeaders().add("Cookie", "XSRF-TOKEN=" + csrfToken.getToken());
          return execution.execute(request, body);
        }
    ));
    return testRestTemplate;
  }
}
