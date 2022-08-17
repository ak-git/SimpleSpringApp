package com.ak.spring.controller;

import java.util.List;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/controller/persons")
public final class PersonController {
  private final PersonRepository repository;
  private final PasswordEncoder encoder;

  @Autowired
  public PersonController(@NonNull PersonRepository repository, @NonNull PasswordEncoder encoder) {
    this.repository = repository;
    this.encoder = encoder;
  }

  @GetMapping("/")
  @ResponseBody
  @NonNull
  public List<Person> list() {
    return repository.findAllPersons();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  @NonNull
  public Person create(@RequestBody @NonNull String name) {
    return repository.save(new Person(name, encoder.encode(Strings.EMPTY), Person.Role.USER));
  }

  @PutMapping("/{name}")
  @ResponseBody
  @NonNull
  public ResponseEntity<Person> update(@PathVariable("name") @NonNull String name, @RequestBody @NonNull String newRawPassword) {
    return repository.findByUUID(Person.nameToUUID(name))
        .filter(person -> encoder.matches(Strings.EMPTY, person.getPassword()))
        .map(p -> new Person(p.getName(), encoder.encode(newRawPassword), Person.Role.USER))
        .map(repository::save)
        .map(p -> new ResponseEntity<>(p, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @NonNull
  public Person delete(@PathVariable("name") @NonNull String name) {
    return repository.save(new Person(name, Strings.EMPTY, Person.Role.NONE));
  }
}
