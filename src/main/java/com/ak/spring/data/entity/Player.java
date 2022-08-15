package com.ak.spring.data.entity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;

import com.ak.util.Strings;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.lang.NonNull;

@Entity
public final class Player extends AbstractRevisionable {
  public enum Gender {
    MALE, FEMALE
  }

  private String firstName = Strings.EMPTY;
  private String surName = Strings.EMPTY;
  private String lastName = Strings.EMPTY;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthDate = LocalDate.EPOCH;
  @Convert(converter = GenderConverter.class)
  private Gender gender = Gender.MALE;

  private static class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
      return Optional.ofNullable(attribute).orElse(Gender.MALE).name();
    }

    @Override
    public Gender convertToEntityAttribute(@NonNull String dbData) {
      return Gender.valueOf(dbData);
    }
  }

  public Player() {
    super();
  }

  public Player(@NonNull UUID uuid) {
    super(uuid);
  }

  @NonNull
  public Player copyInstance() {
    Player p = new Player(getUUID());
    p.firstName = firstName;
    p.surName = surName;
    p.lastName = lastName;
    p.birthDate = birthDate;
    return p;
  }

  public void setFirstName(@NonNull String firstName) {
    this.firstName = firstName;
  }

  public void setSurName(@NonNull String surName) {
    this.surName = surName;
  }

  public void setLastName(@NonNull String lastName) {
    this.lastName = lastName;
  }

  public void setBirthDate(@NonNull LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void setGender(@NonNull Gender gender) {
    this.gender = gender;
  }

  @NonNull
  public String getFirstName() {
    return firstName;
  }

  @NonNull
  public String getSurName() {
    return surName;
  }

  @NonNull
  public String getLastName() {
    return lastName;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  @NonNull
  public Gender getGender() {
    return gender;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player player)) {
      return false;
    }
    return equalsId(player);
  }

  @Override
  public int hashCode() {
    return hashCodeId();
  }

  @Override
  public String toString() {
    return "Player{%s, '%s %s %s', %s, %s}".formatted(super.toString(), firstName, surName, lastName, birthDate, gender);
  }
}
