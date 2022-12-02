package com.ak.spring.data.entity;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;

import com.ak.spring.data.id.RevisionableId;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

@IdClass(RevisionableId.class)
@MappedSuperclass
abstract class AbstractRevisionable {
  @Id
  @Type(type = "uuid-char")
  private final UUID uuid;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long revision;
  private final ZonedDateTime created;

  AbstractRevisionable(@NonNull UUID uuid) {
    this.uuid = uuid;
    created = ZonedDateTime.now();
  }

  @NonNull
  public final UUID getUUID() {
    return uuid;
  }

  public final long getRevision() {
    return revision;
  }

  final boolean equalsId(@NonNull AbstractRevisionable o) {
    return uuid.equals(o.uuid) && revision == o.revision;
  }

  final int hashCodeId() {
    return Objects.hash(uuid, revision);
  }

  @Override
  public String toString() {
    return "{uuid=%s, revision=%d, created=%s}".formatted(uuid, revision, created);
  }
}
