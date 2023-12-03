package com.ak.spring;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
  @Test
  void testMain() {
    Assertions.assertThatNoException().isThrownBy(() -> Application.main(new String[] {}));
  }
}

