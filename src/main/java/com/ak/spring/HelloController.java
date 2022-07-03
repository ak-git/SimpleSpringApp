package com.ak.spring;

import java.util.logging.Logger;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/")
  @NonNull
  public String index() {
    String time = Long.toString(System.nanoTime());
    Logger.getLogger(getClass().getName()).info(() -> time);
    return "Greetings from Spring Boot %s !".formatted(time);
  }
}