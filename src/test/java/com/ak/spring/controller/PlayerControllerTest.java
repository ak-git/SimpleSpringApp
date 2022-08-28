package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, PlayerController.class, SpringSecurityConfig.class})
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
  private PlayerRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @ParameterizedTest
  @ValueSource(strings = {"/controller/players/history/", "/controller/players/"})
  void testNoLoginGet(@NonNull String address) throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.get(address).accept(MediaType.APPLICATION_JSON)));
  }

  @Test
  void testNoLoginPostPut() throws Exception {
    PlayerController.PlayerRecord playerRecord = player("someone");
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.post("/controller/players/")
        .content(mapper.writeValueAsString(playerRecord))
        .contentType(MediaType.APPLICATION_JSON)
    ));
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.put("/controller/players/")
        .content(mapper.writeValueAsString(playerRecord))
        .contentType(MediaType.APPLICATION_JSON)
    ));
  }

  @Test
  void testNoLoginDelete() throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.delete("/controller/players/")));
  }

  private ResultActions checkUnauthorized(@NonNull MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mvc.perform(requestBuilder.with(csrf())).andDo(print()).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "adminTestGetHistoryByUUID", roles = {"ADMIN", "USER"})
  void testGetHistoryByUUID() throws Exception {
    PlayerController.PlayerRecord playerRecord = player("adminTestGetHistoryByUUID");
    Player player = create(playerRecord);
    PlayerController.PlayerRecord second = new PlayerController.PlayerRecord(
        "adminTestGetHistoryByUUID",
        playerRecord.firstName(), "second", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player p2 = update(player.getUUID(), second);
    PlayerController.PlayerRecord third = new PlayerController.PlayerRecord(
        "adminTestGetHistoryByUUID",
        playerRecord.firstName(), "third", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE);
    Player p3 = update(player.getUUID(), third);

    Player[] players = checkHistory(player.getUUID(), third, second, playerRecord);
    assertThat(players).hasSize(3);
    assertThat(players[0]).isEqualTo(p3).isNotEqualTo(p2).isNotEqualTo(player);
    assertThat(players[players.length - 1]).isEqualTo(player).isNotEqualTo(p2).isNotEqualTo(p3);
  }

  @Test
  @WithMockUser("USER")
  void testCreate() throws Exception {
    assertNotNull(create(player("userTestCreate")));
  }

  @Test
  @WithMockUser("USER")
  void testInvalidUUID() throws Exception {
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/players/%s".formatted("invalidUser")).accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("[]"))
    );
  }

  @Test
  @WithMockUser(username = "adminTestUpdate", roles = {"ADMIN", "USER"})
  void testUpdate() throws Exception {
    PlayerController.PlayerRecord playerRecord = player("adminTestUpdate");
    int size = list("adminTestUpdate").size();
    Player player1 = create(playerRecord);
    PlayerController.PlayerRecord playerRecord2 = new PlayerController.PlayerRecord("adminTestUpdate",
        playerRecord.firstName(), "V2", playerRecord.lastName(), LocalDate.parse("1981-07-03"), Player.Gender.MALE
    );
    Player player2 = update(player1.getUUID(), playerRecord2);
    assertThat(player1).isNotEqualTo(player2);
    assertThat(checkHistory(player1.getUUID(), playerRecord2, playerRecord)).hasSize(2);
    assertThat(list("adminTestUpdate")).hasSize(size);
  }

  @Test
  @WithMockUser(username = "adminTestDelete", roles = {"ADMIN", "USER"})
  void testDelete() throws Exception {
    PlayerController.PlayerRecord playerRecord = player("adminTestDelete");
    int size = list("adminTestDelete").size();
    UUID uuid = create(playerRecord).getUUID();
    assertThat(checkHistory(uuid, playerRecord)).hasSize(1);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .delete("/controller/players/%s".formatted(uuid)).with(csrf()))
            .andDo(print())
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.uuid", notNullValue()))
    );
    var empty = new PlayerController.PlayerRecord("adminTestDelete", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, LocalDate.EPOCH, Player.Gender.MALE);
    assertThat(checkHistory(uuid, empty, playerRecord)).hasSize(2);
    assertThat(list("adminTestDelete")).hasSize(size);
  }

  @NonNull
  private Player create(@NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    return check(MockMvcRequestBuilders.post("/controller/players/")
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON).with(csrf()),
        playerRecord
    );
  }

  @NonNull
  private List<Player> list(@NonNull String userName) throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/controller/players/%s".formatted(userName))
            .contentType(MediaType.APPLICATION_JSON).with(csrf()))
        .andReturn().getResponse();
    return List.of(mapper.reader().readValue(response.getContentAsString(), Player[].class));
  }

  private Player update(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    return check(MockMvcRequestBuilders.put("/controller/players/%s".formatted(uuid))
            .content(mapper.writeValueAsString(playerRecord))
            .contentType(MediaType.APPLICATION_JSON),
        playerRecord
    );
  }

  private Player check(@NonNull MockHttpServletRequestBuilder requestBuilder,
                       @NonNull PlayerController.PlayerRecord playerRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(requestBuilder.with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(playerRecord.firstName())))
        .andExpect(jsonPath("$.surName", is(playerRecord.surName())))
        .andExpect(jsonPath("$.lastName", is(playerRecord.lastName())))
        .andExpect(jsonPath("$.birthDate", is(playerRecord.birthDate().toString())))
        .andExpect(jsonPath("$.gender", is(playerRecord.gender().toString())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    return mapper.reader().readValue(response.getContentAsString(), Player.class);
  }

  private Player[] checkHistory(@NonNull UUID uuid, @NonNull PlayerController.PlayerRecord... records) throws Exception {
    ResultActions actions = mvc.perform(MockMvcRequestBuilders
            .get("/controller/players/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON).with(csrf()))
        .andDo(print())
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
    return mapper.reader().readValue(actions.andReturn().getResponse().getContentAsString(), Player[].class);
  }

  private static PlayerController.PlayerRecord player(@NonNull String ownerName) {
    return new PlayerController.PlayerRecord(
        ownerName,
        "Alexander", "V", "K", LocalDate.parse("1981-07-03"), Player.Gender.MALE);
  }
}