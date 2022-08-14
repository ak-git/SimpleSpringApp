package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import com.ak.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller/players")
public final class PlayerController {
  public record PlayerRecord(String firstName, String surName, String lastName,
                             LocalDate birthDate, Player.Gender gender) {
    @NonNull
    Player toPlayer(@NonNull Supplier<Player> p) {
      Player player = p.get();
      player.setFirstName(Optional.ofNullable(firstName).orElse(Strings.EMPTY));
      player.setSurName(Optional.ofNullable(surName).orElse(Strings.EMPTY));
      player.setLastName(Optional.ofNullable(lastName).orElse(Strings.EMPTY));
      player.setBirthDate(Optional.ofNullable(birthDate).orElse(LocalDate.EPOCH));
      player.setGender(Optional.ofNullable(gender).orElse(Player.Gender.MALE));
      return player;
    }
  }

  private final PlayerRepository playerRepository;

  @Autowired
  public PlayerController(@NonNull PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @GetMapping("/history/{uuid}")
  @ResponseBody
  @NonNull
  public List<Player> getPlayerHistoryByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return playerRepository.historyForUUID(uuid);
  }

  @GetMapping("/{uuid}")
  @ResponseBody
  @NonNull
  public ResponseEntity<Player> getPlayerByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    Player player = playerRepository.findByUUID(uuid);
    return player == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(player, HttpStatus.OK);
  }

  @GetMapping("/")
  @ResponseBody
  @NonNull
  public List<Player> players() {
    return playerRepository.findAllPlayers();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public Player createPlayer(@RequestBody @NonNull PlayerRecord p) {
    return playerRepository.save(p.toPlayer(Player::new));
  }

  @PutMapping("/{uuid}")
  @ResponseStatus(HttpStatus.OK)
  public Player updatePlayer(@PathVariable("uuid") @NonNull UUID uuid, @RequestBody @NonNull PlayerRecord p) {
    return playerRepository.save(p.toPlayer(() -> new Player(uuid)));
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Player deletePlayer(@PathVariable("uuid") @NonNull UUID uuid) {
    return playerRepository.save(new Player(uuid));
  }
}
