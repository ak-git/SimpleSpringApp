package com.ak.spring.data.entity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.ak.util.Strings;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.lang.NonNull;

@Entity
public final class Player extends AbstractRevisionable {
  public enum Gender {
    MALE, FEMALE
  }

  private String firstName;
  private String surName;
  private String lastName;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthDate;
  @Convert(converter = GenderConverter.class)
  private Gender gender;
  @ManyToOne
  @JsonIgnore
  private Person owner;

  private static class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
      return Optional.ofNullable(attribute).orElse(Gender.MALE).name();
    }

    @Override
    public Gender convertToEntityAttribute(@NonNull String dbData) {
      return Gender.valueOf(dbData.strip());
    }
  }

  public Player() {
    this(new Builder(null, UUID.randomUUID()));
  }

  private Player(@NonNull Builder b) {
    super(b.uuid);
    owner = b.owner;
    firstName = b.firstName;
    surName = b.surName;
    lastName = b.lastName;
    birthDate = b.birthDate;
    gender = b.gender;
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
    return "Player{%s, '%s %s %s', %s, %s, owner=%s}".formatted(super.toString(), firstName, surName, lastName, birthDate, gender,
        Optional.ofNullable(owner).map(Person::getName).orElse(Strings.EMPTY));
  }

  public static class Builder {
    private final Person owner;
    private final UUID uuid;
    private String firstName = Strings.EMPTY;
    private String surName = Strings.EMPTY;
    private String lastName = Strings.EMPTY;
    private LocalDate birthDate = LocalDate.EPOCH;
    private Gender gender = Gender.MALE;

    public Builder(Person owner, @NonNull UUID uuid) {
      this.owner = owner;
      this.uuid = uuid;
    }

    public Builder copy(@NonNull Player player) {
      firstName = player.firstName;
      surName = player.surName;
      lastName = player.lastName;
      birthDate = player.birthDate;
      gender = player.gender;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = Strings.emptyIfNull(firstName);
      return this;
    }

    public Builder surName(String surName) {
      this.surName = Strings.emptyIfNull(surName);
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = Strings.emptyIfNull(lastName);
      return this;
    }

    public Builder birthDate(LocalDate birthDate) {
      this.birthDate = Optional.ofNullable(birthDate).orElse(LocalDate.EPOCH);
      return this;
    }

    public Builder gender(Gender gender) {
      this.gender = Optional.ofNullable(gender).orElse(Gender.MALE);
      return this;
    }

    public Player build() {
      return new Player(this);
    }
  }
}
