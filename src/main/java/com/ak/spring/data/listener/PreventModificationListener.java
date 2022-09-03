package com.ak.spring.data.listener;

import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public final class PreventModificationListener {
  @PreUpdate
  @PreRemove
  void onPreUpdate(Object o) {
    throw new IllegalStateException("JPA is trying to update or remove an entity %s".formatted(o));
  }
}
