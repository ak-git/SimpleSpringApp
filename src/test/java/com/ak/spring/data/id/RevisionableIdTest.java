package com.ak.spring.data.id;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionableIdTest {
  @Test
  void testEquals() {
    RevisionableId r1 = new RevisionableId();
    RevisionableId r2 = new RevisionableId();

    assertThat(r1).isNotEqualTo(r2).isEqualTo(r1).isNotEqualTo(new Object());
    assertThat(r2).isNotEqualTo(r1).isEqualTo(r2).isNotEqualTo(new Object());
  }

  @Test
  void testHashCode() {
    RevisionableId r1 = new RevisionableId();
    RevisionableId r2 = new RevisionableId();
    assertThat(r1).hasSameHashCodeAs(r1).doesNotHaveSameHashCodeAs(r2);
  }
}