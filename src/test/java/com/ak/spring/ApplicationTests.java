package com.ak.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class ApplicationTests {
  @Test
  void testMain() {
    try {
      Application.main(new String[] {});
    }
    catch (Exception ex) {
      Assertions.fail(ex);
    }
  }
}

