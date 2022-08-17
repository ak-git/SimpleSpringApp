package com.ak.spring.controller;

import java.util.List;
import java.util.UUID;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.spring.security.PersonDetailsService;
import com.ak.spring.security.SpringSecurityConfig;
import com.ak.util.Strings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
  @ValueSource(strings = "username")
  void testNoLoginPost(@NonNull String userName) throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.post("/controller/persons/")
        .content(mapper.writeValueAsString(toPerson(userName)))
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
  @ValueSource(strings = "username")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testCreate(@NonNull String userName) throws Exception {
    int size = persons().size();
    assertNotNull(create(userName));
    assertThat(persons()).hasSize(size + 1);
  }

  @ParameterizedTest
  @ValueSource(strings = "username")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testDelete(@NonNull String userName) throws Exception {
    int size = persons().size();
    UUID uuid = create(userName).getUUID();
    assertThatNoException().isThrownBy(() -> userDetailsService.loadUserByUsername(userName));
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders
                .delete("/controller/persons/%s".formatted(uuid)).with(csrf()))
            .andDo(print())
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.uuid", notNullValue()))
    );
    assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> userDetailsService.loadUserByUsername(userName));
    assertThat(persons()).hasSize(size);
  }

  private Person create(@NonNull String userName) throws Exception {
    PersonController.PersonRecord personRecord = toPerson(userName);
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

  private Person check(@NonNull MockHttpServletRequestBuilder requestBuilder,
                       @NonNull PersonController.PersonRecord personRecord) throws Exception {
    MockHttpServletResponse response = mvc.perform(requestBuilder.with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid", notNullValue()))
        .andExpect(jsonPath("$.name", is(personRecord.name())))
        .andExpect(jsonPath("$.password", not(Matchers.empty())))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    UserDetails userDetails = userDetailsService.loadUserByUsername(personRecord.name());
    assertAll(userDetails.toString(), () -> {
      assertThat(userDetails.getUsername()).isEqualTo(personRecord.name());
      assertThat(encoder.matches(personRecord.password(), userDetails.getPassword())).isTrue();
    });
    return new ObjectMapper().reader().readValue(response.getContentAsString(), Person.class);
  }

  private PersonController.PersonRecord toPerson(@NonNull String userName) {
    return new PersonController.PersonRecord(userName, Strings.EMPTY);
  }
}