package com.ak.app;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

public class MainApp {
  private MainApp() {
  }

  public static void main(@Nonnull String[] args) {
    Logger.getLogger(MainApp.class.getName()).info(() -> "MainApp.main");
  }
}
