package com.ak.spring.controller;

import java.util.List;
import java.util.UUID;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller/persons")
public final class PersonController {
  public record PersonRecord(String name, String password) {
  }

  private final PersonRepository repository;

  @Autowired
  public PersonController(@NonNull PersonRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/history/{uuid}")
  @ResponseBody
  @NonNull
  public List<Person> getHistoryByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.historyForUUID(uuid);
  }

  @GetMapping("/{uuid}")
  @ResponseBody
  @NonNull
  public ResponseEntity<Person> getByUUID(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.findByUUID(uuid)
        .map(p -> new ResponseEntity<>(p, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }

  @GetMapping("/")
  @ResponseBody
  @NonNull
  public List<Person> getAll() {
    return repository.findAllPersons();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public Person create(@RequestBody @NonNull PersonRecord p) {
    return repository.save(new Person(p.name, p.password, Person.Role.USER));
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Person delete(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.save(
        new Person(repository.findByUUID(uuid).map(Person::getName).orElse(uuid.toString()), Strings.EMPTY, Person.Role.NONE)
    );
  }
}
