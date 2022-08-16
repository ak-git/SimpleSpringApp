package com.ak.spring.data;

import java.util.List;
import java.util.stream.IntStream;

import com.ak.spring.Application;
import com.ak.spring.data.entity.Person;
import com.ak.spring.data.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {Application.class, PersonRepository.class})
@EnableJpaRepositories(basePackages = "com.ak.spring.data.repository")
@EntityScan(basePackages = "com.ak.spring.data.entity")
class PersonRepositoryTest {
  @Autowired
  private PersonRepository repository;

  @BeforeEach
  void setUp() {
    int size = 2;
    List<Person> entities = IntStream.range(0, size).mapToObj(value -> new Person()).toList();
    repository.saveAll(entities);
  }

  @Test
  void testPerson() {
    List<Person> persons = repository.findAll();

    assertAll(persons.toString(), () -> {
      assertThat(persons).isNotEmpty();
      Person p1 = persons.get(0);
      Person p2 = persons.get(1);
      Person p3 = new Person(p1.getName(), p1.getPassword(), p1.getRole());
      assertThat(p1).isNotEqualTo(p2).isNotEqualTo(p3).isNotEqualTo(new Object()).isEqualTo(p1);
      assertThat(new Object()).isNotEqualTo(p1);
      assertThat(p1).doesNotHaveSameHashCodeAs(p2).doesNotHaveSameHashCodeAs(p3).hasSameHashCodeAs(p1);
      assertThat(p1).doesNotHaveToString(p2.toString()).doesNotHaveToString(p3.toString());
    });
  }
}