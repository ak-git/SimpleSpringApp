package com.ak.spring.data.entity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;

import com.ak.util.Strings;
import org.springframework.lang.NonNull;

@Entity
public final class Person extends AbstractRevisionable {
  public enum Role {
    ADMIN, USER
  }

  private String name = Strings.EMPTY;
  private String password = Strings.EMPTY;
  private boolean active = true;
  @Convert(converter = RoleConverter.class)
  private Role role = Role.USER;

  private static class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
      return Optional.ofNullable(attribute).orElse(Role.USER).name();
    }

    @Override
    public Role convertToEntityAttribute(@NonNull String dbData) {
      return Role.valueOf(dbData);
    }
  }

  public Person() {
    super();
  }

  public Person(@NonNull String name, @NonNull String password) {
    super(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)));
    this.name = name;
    this.password = password;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setRole(@NonNull Role role) {
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public boolean isActive() {
    return active;
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
    return "Person{%s, %s active=%s, role=%s}".formatted(super.toString(), password, active, role);
  }
}
