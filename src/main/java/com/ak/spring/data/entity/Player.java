package com.ak.spring.data.entity;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.ak.spring.data.id.RevisionableId;
import com.ak.util.Strings;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

@Entity
@IdClass(RevisionableId.class)
public final class Player {
  public enum Gender {
    MALE, FEMALE
  }

  @Id
  @Type(type = "uuid-char")
  private UUID uuid;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long revision;
  private ZonedDateTime created;
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
    this(UUID.randomUUID());
  }

  public Player(@NonNull UUID uuid) {
    this.uuid = uuid;
    created = ZonedDateTime.now();
  }

  @NonNull
  public Player copyInstance() {
    Player p = new Player();
    p.uuid = uuid;
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
  public UUID getUUID() {
    return uuid;
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

  public long getRevision() {
    return revision;
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
    return uuid.equals(player.uuid) && revision == player.revision;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, revision);
  }

  @Override
  public String toString() {
    return "Player{uuid=%s, revision=%d, created=%s, '%s %s %s', %s, %s}"
        .formatted(uuid, revision, created, firstName, surName, lastName, birthDate, gender);
  }
}
