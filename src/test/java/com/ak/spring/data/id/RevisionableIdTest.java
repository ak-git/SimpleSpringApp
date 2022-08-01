package com.ak.spring.data.id;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionableIdTest {
  @Test
  void testEquals() {
    UUID uuid = UUID.randomUUID();
    RevisionableId r1 = new RevisionableId(uuid, 1);
    RevisionableId r2 = new RevisionableId(uuid, 2);
    RevisionableId r3 = new RevisionableId(UUID.randomUUID(), 1);
    RevisionableId r4 = new RevisionableId(UUID.randomUUID(), 1);
    RevisionableId r5 = new RevisionableId(uuid, 1);

    assertThat(r1).isNotEqualTo(r2).isNotEqualTo(r3).isNotEqualTo(r4).isEqualTo(r5).isEqualTo(r1).isNotEqualTo(new Object());
    assertThat(r2).isNotEqualTo(r1).isEqualTo(r2).isNotEqualTo(new Object());
    assertThat(r3).isNotEqualTo(r4);
  }

  @Test
  void testHashCode() {
    RevisionableId r1 = new RevisionableId();
    RevisionableId r2 = new RevisionableId();
    assertThat(r1).hasSameHashCodeAs(r1).doesNotHaveSameHashCodeAs(r2);
  }

  @Test
  void testtoString() {
    UUID uuid = UUID.randomUUID();
    assertThat(new RevisionableId(uuid, Long.MAX_VALUE))
        .hasToString("RevisionableId{uuid=%s, revision=%d}".formatted(uuid, Long.MAX_VALUE));
  }
}