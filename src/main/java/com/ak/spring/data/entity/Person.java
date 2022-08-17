package com.ak.spring.data.entity;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;

import com.ak.util.Strings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.lang.NonNull;

@Entity
@JsonIgnoreProperties({"uuid", "password"})
public final class Person extends AbstractRevisionable {
  public enum Role {
    ADMIN, USER, NONE;

    public static String[] all() {
      return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
  }

  private String name = Strings.EMPTY;
  private String password = Strings.EMPTY;
  @Convert(converter = RoleConverter.class)
  private Role role = Role.NONE;

  private static class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
      return Optional.ofNullable(attribute).orElse(Role.NONE).name();
    }

    @Override
    public Role convertToEntityAttribute(@NonNull String dbData) {
      return Role.valueOf(dbData);
    }
  }

  public Person() {
  }

  public Person(@NonNull String name, @NonNull String password, @NonNull Role role) {
    super(nameToUUID(name));
    this.name = name;
    this.password = password;
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
    return "Person{%s, password=%s, role=%s}".formatted(super.toString(), password, role);
  }

  public static UUID nameToUUID(@NonNull String name) {
    return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
  }
}
