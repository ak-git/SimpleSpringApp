package com.ak.util;

import java.util.Optional;

import org.springframework.lang.NonNull;

public enum Strings {
  ;
  public static final String EMPTY = "";

  public static final String NEW_LINE = String.format("%n");

  @NonNull
  public static String emptyIfNull(String s) {
    return Optional.ofNullable(s).orElse(Strings.EMPTY);
  }
}
