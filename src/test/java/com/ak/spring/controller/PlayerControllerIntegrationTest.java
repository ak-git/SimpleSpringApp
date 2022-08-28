package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.data.entity.Player;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PlayerController.class, SpringSecurityConfig.class, PersonDetailsService.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerControllerIntegrationTest extends AbstractControllerIntegrationTest {
  private static final String ADMIN = "testAdminPlayerController";
  private static final String USER = "testUserPlayerController";

  @Override
  public Iterable<Person> apply(PasswordEncoder encoder) {
    return List.of(
        new Person(ADMIN, encoder.encode("password"), Person.Role.ADMIN),
        new Person(USER, encoder.encode("password"), Person.Role.USER)
    );
  }

  @Test
  void index() {
    int size = withAuth(USER).getForObject("/controller/players/%s".formatted(USER), Player[].class).length;
    PlayerController.PlayerRecord playerRecord = new PlayerController.PlayerRecord(
        USER, "Alexander", Strings.EMPTY, Strings.EMPTY, LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player player = withAuth(USER).postForObject("/controller/players/", playerRecord, Player.class);
    checkEquals(player, playerRecord);
    assertThat(withAuth(USER).getForObject("/controller/players/%s".formatted(USER), Player[].class)).hasSize(size + 1);

    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord(
        USER, "Alexander", "V2", "K2", LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    withAuth(USER).put("/controller/players/%s".formatted(player.getUUID()), playerRecord2);
    assertThat(withAuth(USER).getForObject("/controller/players/%s".formatted(USER), Player[].class)).hasSize(size + 1);

    withAuth(USER).delete("/controller/players/%s".formatted(player.getUUID()));
    Player[] history = withAuth(ADMIN).getForObject("/controller/players/history/%s".formatted(player.getUUID()), Player[].class);
    assertThat(history).hasSize(3);
    for (int i = 1; i < history.length; i++) {
      assertThat(history[i].getRevision()).isLessThan(history[i - 1].getRevision());
    }
    assertThat(withAuth(USER).getForObject("/controller/players/%s".formatted(USER), Player[].class)).hasSize(size);
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
}
