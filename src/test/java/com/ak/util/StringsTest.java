package com.ak.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StringsTest {

  @Test
  void values() {
    Assertions.assertThat(Strings.values()).isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"a", "bc"})
  void emptyIfNull(String s) {
    assertNotNull(Strings.emptyIfNull(s));
  }
}