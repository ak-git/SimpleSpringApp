package com.ak.spring.controller;

import java.util.Objects;
import java.util.stream.Stream;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, PlayerController.class})
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
@EnableWebMvc
class PlayerControllerTest {
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private PlayerController controller;

  @ParameterizedTest
  @MethodSource("player")
  void getPlayerHistoryByUUID(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    Player player = Objects.requireNonNull(controller.createPlayer(playerRecord).getBody());
    Player p2 = Objects.requireNonNull(
        controller.updatePlayer(player.getUUID(),
            new PlayerController.PlayerRecord(playerRecord.firstName(), "second", playerRecord.lastName())
        ).getBody()
    );
    Player p3 = Objects.requireNonNull(
        controller.updatePlayer(player.getUUID(),
            new PlayerController.PlayerRecord(playerRecord.firstName(), "third", playerRecord.lastName())
        ).getBody()
    );
    assertThat(controller.getPlayerHistoryByUUID(player.getUUID()).getBody())
        .hasSize(3).first().isEqualTo(p3).isNotEqualTo(p2).isNotEqualTo(player);
    assertThat(controller.getPlayerHistoryByUUID(player.getUUID()).getBody())
        .hasSize(3).last().isEqualTo(player).isNotEqualTo(p2).isNotEqualTo(p3);

    assertNotNull(
        mvc.perform(
                MockMvcRequestBuilders.get("/controller/player/history/%s".formatted(player.getUUID())).accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[*].firstName", hasItems(playerRecord.firstName())))
            .andExpect(jsonPath("$[0].surName", is("third")))
            .andExpect(jsonPath("$[1].surName", is("second")))
            .andExpect(jsonPath("$[2].surName", is(playerRecord.surName())))
            .andExpect(jsonPath("$[*].lastName", hasItems(playerRecord.lastName())))
    );
  }

  @ParameterizedTest
  @MethodSource("player")
  void getPlayerByUUID(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    Player player = controller.createPlayer(playerRecord).getBody();
    assertNotNull(player);
    Assertions.assertAll(player.toString(), () -> {
      assertThat(player.getFirstName()).isEqualTo(playerRecord.firstName());
      assertThat(player.getSurName()).isEqualTo(playerRecord.surName());
      assertThat(player.getLastName()).isEqualTo(playerRecord.lastName());
      assertThat(controller.getPlayerHistoryByUUID(player.getUUID()).getBody()).hasSize(1);
    });

    assertNotNull(
        mvc.perform(
                MockMvcRequestBuilders.get("/controller/player/%s".formatted(player.getUUID())).accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid", is(player.getUUID().toString())))
            .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
            .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
            .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
    );

    Player player2 = controller.updatePlayer(player.getUUID(), new PlayerController.PlayerRecord(playerRecord.firstName(), "V2", playerRecord.lastName())).getBody();
    assertNotNull(player2);
    Assertions.assertAll(player.toString(), () -> {
      assertThat(player2.getFirstName()).isEqualTo(playerRecord.firstName());
      assertThat(player2.getSurName()).isEqualTo("V2");
      assertThat(player2.getLastName()).isEqualTo(playerRecord.lastName());
      assertThat(controller.getPlayerHistoryByUUID(player2.getUUID()).getBody()).hasSize(2);
    });

    assertNotNull(
        mvc.perform(
                MockMvcRequestBuilders.get("/controller/player/%s".formatted(player.getUUID())).accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid", is(player2.getUUID().toString())))
            .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
            .andExpect(jsonPath("$.surName", is("V2")))
            .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
    );
  }

  @ParameterizedTest
  @MethodSource("player")
  void createPlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .post("/controller/player/")
                .content(mapper.writeValueAsString(playerRecord))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid", notNullValue()))
            .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
            .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
            .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
    );
  }

  private static Stream<PlayerController.PlayerRecord> player() {
    return Stream.of(new PlayerController.PlayerRecord("Alexander", "V", "K"));
  }
}