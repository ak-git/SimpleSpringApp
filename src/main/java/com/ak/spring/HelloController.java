package com.ak.spring;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/")
  @NonNull
  public String index() {
    String time = Long.toString(System.nanoTime());
    Logger.getLogger(getClass().getName()).info(() -> time);
    return "Greetings from Spring Boot %s !".formatted(time);
  }

  @GetMapping("/greeting")
  @NonNull
  public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") @NonNull String name) {
    Greeting greeting = new Greeting(counter.incrementAndGet(), "Hello, %s!".formatted(name));
    Logger.getLogger(getClass().getName()).info(greeting::toString);
    return greeting;
  }
}