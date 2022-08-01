package com.ak.spring.data.id;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.hibernate.annotations.Immutable;
import org.springframework.lang.NonNull;

@Immutable
public final class RevisionableId implements Serializable {
  private static final Random RANDOM = new SecureRandom();
  @NonNull
  private final UUID uuid;
  private final long revision;

  public RevisionableId() {
    this(UUID.randomUUID(), RANDOM.nextLong());
  }

  public RevisionableId(@NonNull UUID uuid, long revision) {
    this.uuid = uuid;
    this.revision = revision;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RevisionableId that)) {
      return false;
    }
    return uuid.equals(that.uuid) && revision == that.revision;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, revision);
  }

  @Override
  public String toString() {
    return "RevisionableId{uuid=%s, revision=%d}".formatted(uuid, revision);
  }
}
