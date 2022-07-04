package com.ak.spring;

import org.springframework.lang.NonNull;

public record Greeting(long id, @NonNull String content) {
}
