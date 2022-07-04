package com.ak.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, HelloController.class})
@AutoConfigureMockMvc
class HelloControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void index() throws Exception {
    Assertions.assertNotNull(
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(startsWith("Greetings from Spring Boot")))
    );
  }

  @Test
  void greeting() throws Exception {
    Assertions.assertNotNull(
        mvc.perform(MockMvcRequestBuilders.get("/greeting/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.content", is("Hello, World!")))
    );
    Assertions.assertNotNull(
        mvc.perform(MockMvcRequestBuilders.get("/greeting/").param("name", "Something")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.content", is("Hello, Something!")))
    );
  }
}