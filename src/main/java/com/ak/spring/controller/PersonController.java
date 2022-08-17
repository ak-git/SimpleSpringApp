package com.ak.spring.controller;

import java.util.List;
import java.util.UUID;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder encoder;

  @Autowired
  public PersonController(@NonNull PersonRepository repository, @NonNull PasswordEncoder encoder) {
    this.repository = repository;
    this.encoder = encoder;
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
    return repository.save(new Person(p.name, encoder.encode(Strings.EMPTY), Person.Role.USER));
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Person delete(@PathVariable("uuid") @NonNull UUID uuid) {
    return repository.save(
        new Person(repository.findByUUID(uuid).map(Person::getName).orElse(uuid.toString()), Strings.EMPTY, Person.Role.NONE)
    );
  }
}
