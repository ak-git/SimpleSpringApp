package com.ak.spring.data.id;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.hibernate.annotations.Immutable;
import org.springframework.lang.NonNull;

@Immutable
public final class RevisionableId implements Serializable {
  private static final Random RANDOM = new Random();
  @NonNull
  private final UUID id = UUID.randomUUID();
  private final long revision = RANDOM.nextLong();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RevisionableId that)) {
      return false;
    }
    return revision == that.revision && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, revision);
  }
}
