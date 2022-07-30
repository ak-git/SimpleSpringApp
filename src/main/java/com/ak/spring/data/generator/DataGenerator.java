package com.ak.spring.data.generator;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class DataGenerator {
  private static final Logger LOGGER = Logger.getLogger(DataGenerator.class.getName());
  private static final String NEW_LINE = String.format("%n");

  @Bean
  public CommandLineRunner commandLineRunner(@NonNull PlayerRepository repository) {
    return args -> {
      repository.save(new Player("Jack", "Bauer"));
      repository.save(new Player("Chloe", "O'Brian"));
      repository.save(new Player("Kim", "Bauer"));
      repository.save(new Player("David", "Palmer"));
      repository.save(new Player("Michelle", "Dessler"));

      LOGGER.info(() ->
          "Players found with findAll(): %n %s".formatted(repository.findAll().stream().map(Player::toString)
              .collect(Collectors.joining(NEW_LINE))));
      LOGGER.info(() -> "Found by name 'b':%n%s".formatted(
          repository.search("B").stream().map(Player::toString).collect(Collectors.joining(NEW_LINE)))
      );
    };
  }
}
