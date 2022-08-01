package com.ak.spring.data.entity;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.ak.spring.data.id.RevisionableId;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

@Entity
@IdClass(RevisionableId.class)
public final class Player {
  @Id
  @Type(type = "uuid-char")
  @NonNull
  private UUID uuid = UUID.randomUUID();
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long revision;
  @NonNull
  private ZonedDateTime created = ZonedDateTime.now();
  @NonNull
  private String firstName = "";
  @NonNull
  private String surName = "";
  @NonNull
  private String lastName = "";

  @NonNull
  public Player copyInstance() {
    Player p = new Player();
    p.uuid = uuid;
    p.firstName = firstName;
    p.surName = surName;
    p.lastName = lastName;
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
    return "Player{uuid=%s, revision=%d, created=%s, firstName='%s', surName='%s', lastName='%s'}"
        .formatted(uuid, revision, created, firstName, surName, lastName);
  }
}
