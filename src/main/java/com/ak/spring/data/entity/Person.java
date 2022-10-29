package com.ak.spring.data.entity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import com.ak.spring.data.listener.PreventModificationListener;
import com.ak.util.Strings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

@Entity
@EntityListeners(PreventModificationListener.class)
public final class Person extends AbstractRevisionable {
  public enum Role {
    ADMIN, USER, NONE
  }

  private final String name;
  @JsonIgnore
  private final String password;
  @Convert(converter = RoleConverter.class)
  private final Role role;

  private static class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
      return Optional.ofNullable(attribute).orElse(Role.NONE).name();
    }

    @Override
    public Role convertToEntityAttribute(@NonNull String dbData) {
      return Role.valueOf(dbData.strip());
    }
  }

  public Person() {
    this(Strings.EMPTY, Strings.EMPTY, Role.NONE);
  }

  public Person(@NonNull String name, @NonNull String password, @NonNull Role role) {
    super(nameToUUID(name));
    this.name = name.strip();
    this.password = password.strip();
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Person person)) {
      return false;
    }
    return equalsId(person);
  }

  @Override
  public int hashCode() {
    return hashCodeId();
  }

  @Override
  public String toString() {
    return "Person{%s, name=%s, password=%s, role=%s}".formatted(super.toString(), name, password, role);
  }

  public static UUID nameToUUID(@NonNull String name) {
    return UUID.nameUUIDFromBytes(name.strip().getBytes(StandardCharsets.UTF_8));
  }
}
