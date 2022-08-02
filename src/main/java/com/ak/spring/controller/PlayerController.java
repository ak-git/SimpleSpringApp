package com.ak.spring.controller;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller/player")
public final class PlayerController {
  public record PlayerRecord(@NonNull String firstName, @NonNull String surName, @NonNull String lastName) {
    @NonNull
    Player toPlayer(@NonNull Supplier<Player> p) {
      Player player = p.get();
      player.setFirstName(firstName);
      player.setSurName(surName);
      player.setLastName(lastName);
      return player;
    }
  }

  @NonNull
  private final PlayerRepository playerRepository;

  @Autowired
  public PlayerController(@NonNull PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @GetMapping("/history/{uuid}")
  @NonNull
  public ResponseEntity<List<Player>> getPlayerHistoryByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return new ResponseEntity<>(playerRepository.historyForUUID(uuid), HttpStatus.OK);
  }

  @GetMapping("/{uuid}")
  @NonNull
  public ResponseEntity<Player> getPlayerByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return new ResponseEntity<>(playerRepository.findByUUID(uuid), HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<Player> createPlayer(@RequestBody @NonNull PlayerRecord p) {
    return new ResponseEntity<>(playerRepository.save(p.toPlayer(Player::new)), HttpStatus.OK);
  }

  @PutMapping("/{uuid}")
  public ResponseEntity<Player> updatePlayer(@PathVariable("uuid") UUID uuid, @RequestBody PlayerRecord p) {
    return new ResponseEntity<>(playerRepository.save(p.toPlayer(() -> new Player(uuid))), HttpStatus.OK);
  }
}