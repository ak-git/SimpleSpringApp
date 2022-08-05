package com.ak.spring.data.generator;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class DataGenerator {
  private static final Logger LOGGER = Logger.getLogger(DataGenerator.class.getName());
  private static final String NEW_LINE = String.format("%n");
  private static final Random RANDOM = new SecureRandom();

  @Bean
  public CommandLineRunner commandLineRunner(@NonNull PlayerRepository repository) {
    return args -> {
      if (repository.count() == 0) {
        Faker faker = new Faker(Locale.getDefault());
        LOGGER.info(() -> "Generating players");
        repository.saveAll(IntStream.range(0, 50)
            .mapToObj(value -> {
              Player entity = new Player();
              entity.setFirstName(faker.name().firstName());
              entity.setSurName("");
              entity.setLastName(faker.name().lastName());
              entity.setBirthDate(LocalDate.of(RANDOM.nextInt(1991, 2001),
                  Month.of(RANDOM.nextInt(Month.JANUARY.ordinal(), Month.DECEMBER.ordinal()) + 1),
                  RANDOM.nextInt(1, 28)).toString());
              return entity;
            })
            .toList()
        );
        IntStream.range(0, 2).forEach(value ->
            repository.findAll().stream().filter(player -> RANDOM.nextDouble() < 0.1)
                .forEach(player -> {
                  Player entity = player.copyInstance();
                  entity.setSurName(faker.funnyName().name());
                  repository.save(entity);
                })
        );
        LOGGER.info(() ->
            "Players found with findAll():%n%s".formatted(repository.findAll().stream().map(Player::toString)
                .collect(Collectors.joining(NEW_LINE))));
      }
      else {
        LOGGER.info(() -> "Use existing data, found %d players".formatted(repository.count()));
      }
    };
  }
}
