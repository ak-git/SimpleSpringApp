package com.ak.spring.security;

import com.ak.spring.data.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
  private static final String USER_PATTERN = "/controller/players/**";

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
    return CookieCsrfTokenRepository.withHttpOnlyFalse();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(@NonNull HttpSecurity http) throws Exception {
    http
        .httpBasic()
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/controller/players/history/**").hasRole(Person.Role.ADMIN.name())
        .antMatchers(HttpMethod.GET, USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.POST, USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.PUT, USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.DELETE, USER_PATTERN).hasAnyRole(Person.Role.all())
        .and()
        .formLogin().disable()
        .csrf().csrfTokenRepository(csrfTokenRepository());
    return http.build();
  }
}