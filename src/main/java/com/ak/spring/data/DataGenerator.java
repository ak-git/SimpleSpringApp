package com.ak.spring.data;

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

import static com.ak.util.Strings.NEW_LINE;

@Service
public class DataGenerator {
  private static final Logger LOGGER = Logger.getLogger(DataGenerator.class.getName());
  private static final Random RANDOM = new SecureRandom();

  @Bean
  public CommandLineRunner generatePlayers(@NonNull PlayerRepository repository) {
    return args -> {
      if (repository.count() == 0) {
        Faker faker = new Faker(Locale.getDefault());
        repository.saveAll(IntStream.range(0, 20)
            .mapToObj(
                value -> new Player.Builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .birthDate(LocalDate.of(RANDOM.nextInt(1991, 2001),
                        Month.of(RANDOM.nextInt(Month.JANUARY.ordinal(), Month.DECEMBER.ordinal()) + 1),
                        RANDOM.nextInt(1, 28)))
                    .gender(Player.Gender.valueOf(faker.demographic().sex().toUpperCase()))
                    .build()
            )
            .toList()
        );
        repository.findAll().stream().filter(player -> RANDOM.nextDouble() < 0.2)
            .forEach(player -> repository.save(new Player.Builder(player).surName(faker.funnyName().name()).build()));
        LOGGER.info(() ->
            "Players found:%n%s".formatted(repository.findAll().stream().map(Player::toString)
                .collect(Collectors.joining(NEW_LINE))));
      }
      else {
        LOGGER.info(() -> "Use existing data, found %d players".formatted(repository.count()));
      }
    };
  }
}
