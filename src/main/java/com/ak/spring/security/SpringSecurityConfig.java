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
  private static final String PERSON_PATTERN = "/controller/persons/**";
  private static final String PLAYER_USER_PATTERN = "/controller/players/**";

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
        .antMatchers(HttpMethod.GET, PERSON_PATTERN).permitAll()
        .antMatchers(HttpMethod.POST, PERSON_PATTERN).hasRole(Person.Role.ADMIN.name())
        .antMatchers(HttpMethod.DELETE, PERSON_PATTERN).hasRole(Person.Role.ADMIN.name())
        .antMatchers(HttpMethod.GET, "/controller/players/history/**").hasRole(Person.Role.ADMIN.name())
        .antMatchers(HttpMethod.GET, PLAYER_USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.POST, PLAYER_USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.PUT, PLAYER_USER_PATTERN).hasAnyRole(Person.Role.all())
        .antMatchers(HttpMethod.DELETE, PLAYER_USER_PATTERN).hasAnyRole(Person.Role.all())
        .and()
        .formLogin().disable()
        .csrf().csrfTokenRepository(csrfTokenRepository());
    return http.build();
  }
}