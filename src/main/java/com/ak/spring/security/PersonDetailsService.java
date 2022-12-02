package com.ak.spring.security;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.ak.util.Strings.NEW_LINE;

@Service
public class PersonDetailsService implements UserDetailsService {
  private static final Logger LOGGER = Logger.getLogger(PersonDetailsService.class.getName());
  private final PersonRepository repository;

  @Autowired
  public PersonDetailsService(@NonNull PersonRepository repository) {
    this.repository = repository;
    LOGGER.info(() -> "Users:%n%s".formatted(repository.findAll().stream().map(Person::toString).collect(Collectors.joining(NEW_LINE))));
  }

  @Override
  public UserDetails loadUserByUsername(@NonNull String userName) throws UsernameNotFoundException {
    return repository.findByUUID(Person.nameToUUID(userName))
        .map(person -> User.withUsername(person.getName()).password(person.getPassword()).roles(person.getRole().name()).build())
        .orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
  }
}
