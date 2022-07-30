package com.ak.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNull;

@SpringBootApplication
public class Application {
  public static void main(@NonNull String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
