package com.ak.spring.data.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

@Entity
public final class Player {
  @Id
  @GeneratedValue
  @Type(type = "uuid-char")
  private UUID id;
  private String firstName = "";
  private String lastName = "";

  public Player() {
  }

  public Player(@NonNull String firstName, @NonNull String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return "Player{id=%s, firstName='%s', lastName='%s'}".formatted(id, firstName, lastName);
  }
}
