package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
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

  @ParameterizedTest
  @MethodSource("player")
  void testGetPlayerHistoryByUUID(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    Player player = createPlayer(playerRecord);
    PlayerController.PlayerRecord second = new PlayerController.PlayerRecord(
        playerRecord.firstName(), "second", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player p2 = updatePlayer(player.getUUID(), second);
    PlayerController.PlayerRecord third = new PlayerController.PlayerRecord(
        playerRecord.firstName(), "third", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player p3 = updatePlayer(player.getUUID(), third);

    Player[] players = checkHistory(player.getUUID(), third, second, playerRecord);
    assertThat(players).hasSize(3);
    assertThat(players[0]).isEqualTo(p3).isNotEqualTo(p2).isNotEqualTo(player);
    assertThat(players[players.length - 1]).isEqualTo(player).isNotEqualTo(p2).isNotEqualTo(p3);
  }

  @ParameterizedTest
  @MethodSource("player")
  void testCreatePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    int size = players().size();
    assertNotNull(createPlayer(playerRecord));
    assertThat(players()).hasSize(size + 1);
  }

  @ParameterizedTest
  @MethodSource("player")
  void testGetPlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    Player player1 = createPlayer(playerRecord);
    Player player2 = getPlayerByUUID(player1.getUUID(), playerRecord);
    assertThat(player1).isEqualTo(player2);
  }

  @Test
  void testInvalidUUID() throws Exception {
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/players/%s".formatted(UUID.randomUUID())).accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent())
            .andExpect(content().string(""))
    );
  }

  @ParameterizedTest
  @MethodSource("player")
  void testUpdatePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    int size = players().size();
    Player player1 = createPlayer(playerRecord);
    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord(
        playerRecord.firstName(), "V2", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE
    );
    Player player2 = updatePlayer(player1.getUUID(), playerRecord2);
    assertThat(player1).isNotEqualTo(player2);
    assertThat(checkHistory(player1.getUUID(), playerRecord2, playerRecord)).hasSize(2);
    assertThat(players()).hasSize(size + 1);
  }

  @ParameterizedTest
  @MethodSource("player")
  void testDeletePlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    int size = players().size();
    UUID uuid = createPlayer(playerRecord).getUUID();
    assertThat(checkHistory(uuid, playerRecord)).hasSize(1);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .delete("/controller/players/%s".formatted(uuid)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.uuid", notNullValue()))
    );
    var empty = new PlayerController.PlayerRecord("", "", "", LocalDate.EPOCH, Player.Gender.MALE);
    assertThat(checkHistory(uuid, empty, playerRecord)).hasSize(2);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/players/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent())
            .andExpect(content().string(""))
    );
    assertThat(players()).hasSize(size);
  }

  @NonNull
  private Player createPlayer(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    return check(MockMvcRequestBuilders.post("/controller/players/")
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON),
        playerRecord
    );
  }

  @NonNull
  private List<Player> players() throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/controller/players/").contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    return List.of(new ObjectMapper().reader().readValue(response.getContentAsString(), Player[].class));
  }

  private Player getPlayerByUUID(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    return check(MockMvcRequestBuilders.get("/controller/players/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON),
        playerRecord
    );
  }

  private Player updatePlayer(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    return check(MockMvcRequestBuilders.put("/controller/players/%s".formatted(uuid))
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON),
        playerRecord
    );
  }

  private Player check(MockHttpServletRequestBuilder requestBuilder, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(requestBuilder)
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
        .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
        .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
        .andExpect(jsonPath("$.birthDate", is(playerRecord.birthDate().toString())))
        .andExpect(jsonPath("$.gender", is(playerRecord.gender().toString())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    return new ObjectMapper().reader().readValue(response.getContentAsString(), Player.class);
  }

  private Player[] checkHistory(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord... records) throws Exception {
    ResultActions actions = mvc.perform(MockMvcRequestBuilders
            .get("/controller/players/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(records.length)));

    for (int i = 0; i < records.length; i++) {
      PlayerController.PlayerRecord record = records[i];
      actions
          .andExpect(jsonPath("$[%d].uuid".formatted(i), notNullValue()))
          .andExpect(jsonPath("$[%d].firstName".formatted(i), is(record.firstName())))
          .andExpect(jsonPath("$[%d].surName".formatted(i), is(record.surName())))
          .andExpect(jsonPath("$[%d].lastName".formatted(i), is(record.lastName())))
          .andExpect(jsonPath("$[%d].birthDate".formatted(i), is(record.birthDate().toString())))
          .andExpect(jsonPath("$[%d].gender".formatted(i), is(record.gender().toString())));
    }
    return new ObjectMapper().reader().readValue(actions.andReturn().getResponse().getContentAsString(), Player[].class);
  }

  private static Stream<PlayerController.PlayerRecord> player() {
    return Stream.of(new PlayerController.PlayerRecord(
        "Alexander", "V", "K", LocalDate.parse("1981-07-03"), Player.Gender.MALE)
    );
  }
}