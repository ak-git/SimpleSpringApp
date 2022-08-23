package com.ak.spring.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.spring.data.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
@RequestMapping("/controller/players")
public final class PlayerController {
  public record PlayerRecord(String ownerName, String firstName, String surName, String lastName,
                             LocalDate birthDate, Player.Gender gender) {
    @NonNull
    Player toPlayer(@NonNull Supplier<Player.Builder> b) {
      return b.get().firstName(firstName).surName(surName).lastName(lastName).birthDate(birthDate).gender(gender).build();
    }
  }

  private final PlayerRepository repository;
  private final PersonRepository personRepository;

  @Autowired
  public PlayerController(@NonNull PlayerRepository repository, @NonNull PersonRepository personRepository) {
    this.repository = repository;
    this.personRepository = personRepository;
  }

  @GetMapping("/history/{uuid}")
  @ResponseBody
  @NonNull
  public List<Player> getHistoryByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.historyForUUID(uuid);
  }

  @GetMapping("/{uuid}")
  @ResponseBody
  @NonNull
  public ResponseEntity<Player> getByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.findByUUID(uuid)
        .map(p -> new ResponseEntity<>(p, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }

  @GetMapping("/")
  @ResponseBody
  @NonNull
  public List<Player> list() {
    return repository.findAllPlayers();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  @NonNull
  public Player create(@RequestBody @NonNull PlayerRecord p) {
    return repository.save(p.toPlayer(() -> new Player.Builder(personRepository.findByUUID(Person.nameToUUID(p.ownerName)).orElse(null), UUID.randomUUID())));
  }

  @PutMapping("/{uuid}")
  @ResponseStatus(HttpStatus.OK)
  @NonNull
  public Player update(@PathVariable("uuid") @NonNull UUID uuid, @RequestBody @NonNull PlayerRecord p) {
    return repository.save(p.toPlayer(() -> new Player.Builder(personRepository.findByUUID(Person.nameToUUID(p.ownerName)).orElse(null), uuid)));
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @NonNull
  public Player delete(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.save(new Player.Builder(null, uuid).build());
  }
}
