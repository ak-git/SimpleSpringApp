package com.ak.spring.data;

import java.util.List;
import java.util.stream.IntStream;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PlayerRepository.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PlayerRepositoryTest {
  @Autowired
  private PlayerRepository repository;

  @BeforeEach
  void setUp() {
    int size = 2;
    List<Player> entities = IntStream.range(0, size).mapToObj(value -> new Player()).toList();
    repository.saveAll(entities);
  }

  @Test
  void testPlayer() {
    List<Player> players = repository.findAll();

    assertAll(players.toString(), () -> {
      assertThat(players).isNotEmpty();
      Player p1 = players.get(0);
      Player p2 = players.get(1);
      Player p3 = p1.copyInstance();
      assertThat(p1).isNotEqualTo(p2).isNotEqualTo(p3).isNotEqualTo(new Object()).isEqualTo(p1);
      assertThat(new Object()).isNotEqualTo(p1);
      assertThat(p1).doesNotHaveSameHashCodeAs(p2).doesNotHaveSameHashCodeAs(p3).hasSameHashCodeAs(p1);
      assertThat(p1).doesNotHaveToString(p2.toString()).doesNotHaveToString(p3.toString());
    });
  }
}