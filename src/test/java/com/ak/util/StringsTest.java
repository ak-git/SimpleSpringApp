package com.ak.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StringsTest {

  @Test
  void values() {
    Assertions.assertThat(Strings.values()).isEmpty();
  }
}