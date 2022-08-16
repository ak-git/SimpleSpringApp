package com.ak.spring.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, PersonController.class, SpringSecurityConfig.class, PersonDetailsService.class})
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
@EnableWebMvc
class PersonControllerTest {
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private PersonRepository repository;
  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  private UserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    repository.save(new Person("admin", encoder.encode("password"), Person.Role.ADMIN));
  }


  @ParameterizedTest
  @ValueSource(strings = {"/controller/persons/history/", "/controller/persons/"})
  void testNoLoginGet(@NonNull String address) throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.get(address).accept(MediaType.APPLICATION_JSON)));
  }

  @ParameterizedTest
  @MethodSource("person")
  void testNoLoginPost(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.post("/controller/persons/")
        .content(mapper.writeValueAsString(personRecord))
        .contentType(MediaType.APPLICATION_JSON)
    ));
  }

  @Test
  void testNoLoginDelete() throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.delete("/controller/persons/")));
  }

  private ResultActions checkUnauthorized(@NonNull MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mvc.perform(requestBuilder.with(csrf())).andDo(print()).andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @MethodSource("person")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testGetHistoryByUUID(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    Person person = create(personRecord);
    PersonController.PersonRecord second = new PersonController.PersonRecord(personRecord.name(), "second");
    Person p2 = create(second);
    PersonController.PersonRecord third = new PersonController.PersonRecord(personRecord.name(), "third");
    Person p3 = create(third);

    Person[] persons = checkHistory(person.getUUID(), third, second, personRecord);
    assertThat(persons).hasSize(3);
    assertThat(persons[0]).isEqualTo(p3).isNotEqualTo(p2).isNotEqualTo(person);
    assertThat(persons[persons.length - 1]).isEqualTo(person).isNotEqualTo(p2).isNotEqualTo(p3);
  }

  @ParameterizedTest
  @MethodSource("person")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testCreate(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    int size = persons().size();
    assertNotNull(create(personRecord));
    assertThat(persons()).hasSize(size + 1);
  }

  @ParameterizedTest
  @MethodSource("person")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testGet(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    Person person1 = create(personRecord);
    Person person2 = getByUUID(person1.getUUID(), personRecord);
    assertThat(person1).isEqualTo(person2);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testInvalidUUID() throws Exception {
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/persons/%s".formatted(UUID.randomUUID())).accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andExpect(content().string(Strings.EMPTY))
    );
  }

  @ParameterizedTest
  @MethodSource("person")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testUpdate(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    int size = persons().size();
    Person person1 = create(personRecord);
    PersonController.PersonRecord personRecord2 = new PersonController.PersonRecord(personRecord.name(), "V2");
    Person person2 = create(personRecord2);
    assertThat(person1).isNotEqualTo(person2);
    assertThat(checkHistory(person1.getUUID(), personRecord2, personRecord)).hasSize(2);
    assertThat(persons()).hasSize(size + 1);
  }

  @ParameterizedTest
  @MethodSource("person")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testDelete(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    int size = persons().size();
    UUID uuid = create(personRecord).getUUID();
    assertThat(checkHistory(uuid, personRecord)).hasSize(1);
    String userName = personRecord.name();
    assertThatNoException().isThrownBy(() -> userDetailsService.loadUserByUsername(userName));
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .delete("/controller/persons/%s".formatted(uuid)).with(csrf()))
            .andDo(print())
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.uuid", notNullValue()))
    );
    assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> userDetailsService.loadUserByUsername(userName));
    var empty = new PersonController.PersonRecord(userName, Strings.EMPTY);
    assertThat(checkHistory(uuid, empty, personRecord)).hasSize(2);
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .get("/controller/persons/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andExpect(content().string(Strings.EMPTY))
    );
    assertThat(persons()).hasSize(size);
  }

  private Person create(@NonNull PersonController.PersonRecord personRecord) throws Exception {
    return check(MockMvcRequestBuilders.post("/controller/persons/")
            .content(mapper.writeValueAsString(personRecord))
            .contentType(MediaType.APPLICATION_JSON).with(csrf()),
        personRecord
    );
  }

  @NonNull
  private List<Person> persons() throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/controller/persons/")
            .contentType(MediaType.APPLICATION_JSON).with(csrf()))
        .andReturn().getResponse();
    return List.of(new ObjectMapper().reader().readValue(response.getContentAsString(), Person[].class));
  }

  private Person getByUUID(@NonNull UUID uuid, @NonNull PersonController.PersonRecord personRecord) throws Exception {
    return check(MockMvcRequestBuilders.get("/controller/persons/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON),
        personRecord
    );
  }

  private Person check(@NonNull MockHttpServletRequestBuilder requestBuilder,
                       @NonNull PersonController.PersonRecord personRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(requestBuilder.with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.name", is(personRecord.name())))
        .andExpect(jsonPath("$.password", is(personRecord.password())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    UserDetails userDetails = userDetailsService.loadUserByUsername(personRecord.name());
    assertAll(userDetails.toString(), () -> {
      assertThat(userDetails.getUsername()).isEqualTo(personRecord.name());
      assertThat(userDetails.getPassword()).isEqualTo(personRecord.password());
    });
    return new ObjectMapper().reader().readValue(response.getContentAsString(), Person.class);
  }

  private Person[] checkHistory(@NonNull UUID uuid, @NonNull PersonController.PersonRecord... records) throws Exception {
    ResultActions actions = mvc.perform(MockMvcRequestBuilders
            .get("/controller/persons/history/%s".formatted(uuid)).accept(MediaType.APPLICATION_JSON).with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(records.length)));

    for (int i = 0; i < records.length; i++) {
      PersonController.PersonRecord record = records[i];
      actions
          .andExpect(jsonPath("$[%d].uuid".formatted(i), notNullValue()))
          .andExpect(jsonPath("$[%d].name".formatted(i), is(record.name())))
          .andExpect(jsonPath("$[%d].password".formatted(i), is(record.password())));
    }
    return new ObjectMapper().reader().readValue(actions.andReturn().getResponse().getContentAsString(), Person[].class);
  }

  private static Stream<PersonController.PersonRecord> person() {
    return Stream.of(new PersonController.PersonRecord("ak", "pass"));
  }
}