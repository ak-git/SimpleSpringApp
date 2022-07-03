package com.ak.spring;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      Logger.getLogger(getClass().getName()).info(() -> "Let's inspect the beans provided by Spring Boot:");
      Logger.getLogger(getClass().getName()).info(
          () -> Arrays.stream(ctx.getBeanDefinitionNames()).collect(Collectors.joining(String.format("%n")))
      );
    };
  }
}
