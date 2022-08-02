package com.ak.spring.controller;

import java.util.UUID;
import java.util.stream.Stream;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
  void testGetPlayerHistoryByUUID(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    Player player = getPlayerByUUID(createPlayer(playerRecord), playerRecord);
    Player p2 = updatePlayer(player.getUUID(), new PlayerController.PlayerRecord(playerRecord.firstName(), "second", playerRecord.lastName()));
    Player p3 = updatePlayer(player.getUUID(), new PlayerController.PlayerRecord(playerRecord.firstName(), "third", playerRecord.lastName()));
    assertThat(controller.getPlayerHistoryByUUID(player.getUUID()).getBody())
        .hasSize(3).first().isEqualTo(p3).isNotEqualTo(p2).isNotEqualTo(player);
    assertThat(controller.getPlayerHistoryByUUID(player.getUUID()).getBody())
        .hasSize(3).last().isEqualTo(player).isNotEqualTo(p2).isNotEqualTo(p3);

    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/history/%s".formatted(player.getUUID())).accept(MediaType.APPLICATION_JSON))
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
  void testCreatePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    assertNotNull(createPlayer(playerRecord));
  }

  @Test
  void testInvalidUUID() throws Exception {
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/%s".formatted(UUID.randomUUID())).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""))
    );
  }

  @ParameterizedTest
  @MethodSource("player")
  void testUpdatePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    UUID uuid = createPlayer(playerRecord);
    Player player1 = getPlayerByUUID(uuid, playerRecord);
    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord(
        playerRecord.firstName(), "V2", playerRecord.lastName()
    );
    Player player2 = updatePlayer(uuid, playerRecord2);
    assertThat(player1).isNotEqualTo(player2);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].firstName", hasItems(playerRecord.firstName(), playerRecord2.firstName())))
            .andExpect(jsonPath("$[0].surName", is(playerRecord2.surName())))
            .andExpect(jsonPath("$[1].surName", is(playerRecord.surName())))
            .andExpect(jsonPath("$[*].lastName", hasItems(playerRecord.lastName(), playerRecord2.lastName())))
    );
  }

  @ParameterizedTest
  @MethodSource("player")
  void testDeletePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    UUID uuid = createPlayer(playerRecord);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].firstName", hasItems(playerRecord.firstName())))
            .andExpect(jsonPath("$[0].surName", is(playerRecord.surName())))
            .andExpect(jsonPath("$[*].lastName", hasItems(playerRecord.lastName())))
    );
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .delete("/controller/player/%s".formatted(uuid)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.uuid", notNullValue()))
    );
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].firstName", hasItems(playerRecord.firstName(), "")))
            .andExpect(jsonPath("$[*].surName", hasItems(playerRecord.surName(), "")))
            .andExpect(jsonPath("$[*].lastName", hasItems(playerRecord.lastName(), "")))
    );
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/player/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(MockMvcResultMatchers.content().string(""))
    );
  }

  @NonNull
  private UUID createPlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
            .post("/controller/player/")
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
        .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
        .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();

    Player player = new ObjectMapper().reader().readValue(response.getContentAsString(), Player.class);
    assertThat(player.getRevision()).isPositive();
    return player.getUUID();
  }

  private Player getPlayerByUUID(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
            .get("/controller/player/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", is(uuid.toString())))
        .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
        .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
        .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    return new ObjectMapper().reader().readValue(response.getContentAsString(), Player.class);
  }

  private Player updatePlayer(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
            .put("/controller/player/%s".formatted(uuid))
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
        .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
        .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    return new ObjectMapper().reader().readValue(response.getContentAsString(), Player.class);
  }

  private static Stream<PlayerController.PlayerRecord> player() {
    return Stream.of(new PlayerController.PlayerRecord("Alexander", "V", "K"));
  }
}