package com.ak.spring.data.listener;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.data.entity.Player;
import com.ak.spring.data.repository.PersonRepository;
import com.ak.spring.data.repository.PlayerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

@SpringBootTest(classes = {Application.class, PersonRepository.class, PlayerRepository.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PreventModificationListenerTest {
  @Test
  void onPreUpdatePerson(@Autowired @NonNull PersonRepository repository) {
    check(repository, new Person());
  }

  @Test
  void onPreUpdatePlayer(@Autowired @NonNull PlayerRepository repository) {
    check(repository, new Player());
  }

  private static <T, ID> void check(@NonNull CrudRepository<T, ID> repository, @NonNull T entity) {
    repository.save(entity);
    Assertions.assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(repository::deleteAll);
  }
}