package com.ak.spring.controller;

import java.util.List;

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
    repository.save(new Person("user", encoder.encode(Strings.EMPTY), Person.Role.USER));
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testLoginGetAll() throws Exception {
    assertNotNull(checkUnauthorizedOk(MockMvcRequestBuilders.get("/controller/persons/")));
  }

  @Test
  void testNoLoginGetByName() throws Exception {
    assertNotNull(checkUnauthorizedOk(MockMvcRequestBuilders.get("/controller/persons/user")));
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders.get("/controller/persons/user2").with(csrf()))
            .andDo(print()).andExpect(status().isNoContent())
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"username", "something"})
  void testNoLoginPost(@NonNull String userName) throws Exception {
    assertNotNull(checkUnauthorized(MockMvcRequestBuilders.post("/controller/persons/")
        .content(userName)
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

  private ResultActions checkUnauthorizedOk(@NonNull MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mvc.perform(requestBuilder.with(csrf())).andDo(print()).andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"username", "something"})
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testCreate(@NonNull String userName) throws Exception {
    int size = list().size();
    assertNotNull(check(MockMvcRequestBuilders.post("/controller/persons/")
            .content(userName)
            .contentType(MediaType.APPLICATION_JSON).with(csrf()),
        userName
    ));
    assertThat(list()).hasSize(size + 1);
  }

  @Test
  void testPut() throws Exception {
    assertNotNull(mvc.perform(MockMvcRequestBuilders.put("/controller/persons/invalidUser")
        .content("password")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())).andDo(print()).andExpect(status().isNoContent()));

    assertNotNull(checkUnauthorizedOk(MockMvcRequestBuilders.put("/controller/persons/user")
        .content("password")
        .contentType(MediaType.APPLICATION_JSON)
    ));
    assertNotNull(mvc.perform(MockMvcRequestBuilders.put("/controller/persons/user")
        .content("password2")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())).andDo(print()).andExpect(status().isNoContent()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"username", "something"})
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testDelete(@NonNull String userName) throws Exception {
    int size = list().size();
    assertThatNoException().isThrownBy(() -> userDetailsService.loadUserByUsername("admin"));
    assertNotNull(
        mvc.perform(MockMvcRequestBuilders.delete("/controller/persons/%s".formatted(userName)).with(csrf()))
            .andDo(print())
            .andExpect(status().isAccepted())
    );
    assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> userDetailsService.loadUserByUsername(userName));
    assertThat(list()).hasSize(size);
  }

  @NonNull
  private List<Person> list() throws Exception {
    MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/controller/persons/")
            .contentType(MediaType.APPLICATION_JSON).with(csrf()))
        .andReturn().getResponse();
    return List.of(mapper.reader().readValue(response.getContentAsString(), Person[].class));
  }

  private Person check(@NonNull MockHttpServletRequestBuilder requestBuilder,
                       @NonNull String userName) throws Exception {
    MockHttpServletResponse response = mvc.perform(requestBuilder.with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(userName)))
        .andExpect(jsonPath("$.revision", greaterThan(0)))
        .andReturn().getResponse();
    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
    assertAll(userDetails.toString(), () -> {
      assertThat(userDetails.getUsername()).isEqualTo(userName);
      assertThat(encoder.matches(Strings.EMPTY, userDetails.getPassword())).isTrue();
    });
    return mapper.reader().readValue(response.getContentAsString(), Person.class);
  }
}