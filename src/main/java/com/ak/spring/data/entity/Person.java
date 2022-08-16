package com.ak.spring.data.entity;

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

  public Person(@NonNull UUID uuid) {
    super(uuid);
  }

  @NonNull
  public Person copyInstance() {
    Person p = new Person(getUUID());
    p.name = name;
    p.password = password;
    p.active = active;
    p.role = role;
    return p;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  public void setPassword(@NonNull String password) {
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
    return "Person{%s, %s %s active=%s, role=%s}".formatted(super.toString(), name, password, active, role);
  }
}
