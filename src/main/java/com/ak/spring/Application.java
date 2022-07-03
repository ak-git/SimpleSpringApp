package com.ak.spring;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@SpringBootApplication
public class Application {
  public static void main(@NonNull String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  @NonNull
  public CommandLineRunner commandLineRunner(@NonNull ApplicationContext ctx) {
    return args -> {
      Logger.getLogger(getClass().getName()).info(() -> "Let's inspect the beans provided by Spring Boot:");
      Logger.getLogger(getClass().getName()).info(
          () -> Arrays.stream(ctx.getBeanDefinitionNames()).collect(Collectors.joining(String.format("%n")))
      );
    };
  }
}
