package com.ak.spring.data.entity;

import java.time.ZonedDateTime;
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
  @NonNull
  private ZonedDateTime time = ZonedDateTime.now();
  @NonNull
  private String firstName = "";
  @NonNull
  private String surName = "";
  @NonNull
  private String lastName = "";

  public void setFirstName(@NonNull String firstName) {
    this.firstName = firstName;
  }

  public void setSurName(@NonNull String surName) {
    this.surName = surName;
  }

  public void setLastName(@NonNull String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return "Player{id=%s, time=%s, firstName='%s', surName='%s', lastName='%s'}".formatted(id, time, firstName, surName, lastName);
  }
}
