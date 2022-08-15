package com.ak.spring.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
  private enum Roles {
    ADMIN, USER;

    static String[] all() {
      return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
  }

  private static final String USER_PATTERN = "/controller/players/**";

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
    return CookieCsrfTokenRepository.withHttpOnlyFalse();
  }

  @Bean
  public SecurityFilterChain filterChain(@NonNull HttpSecurity http) throws Exception {
    http
        .httpBasic()
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/controller/players/history/**").hasRole(Roles.ADMIN.name())
        .antMatchers(HttpMethod.GET, USER_PATTERN).hasAnyRole(Roles.all())
        .antMatchers(HttpMethod.POST, USER_PATTERN).hasAnyRole(Roles.all())
        .antMatchers(HttpMethod.PUT, USER_PATTERN).hasAnyRole(Roles.all())
        .antMatchers(HttpMethod.DELETE, USER_PATTERN).hasAnyRole(Roles.all())
        .and()
        .formLogin().disable()
        .csrf().csrfTokenRepository(csrfTokenRepository());
    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return new InMemoryUserDetailsManager(
        User.withUsername("user").password(encoder.encode("password")).roles(Roles.USER.name()).build(),
        User.withUsername("admin").password(encoder.encode("password")).roles(Roles.ADMIN.name()).build()
    );
  }
}