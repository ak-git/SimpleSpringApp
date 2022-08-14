package com.ak.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
  private static final String USER_PATTERN = "/controller/players/**";

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic()
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/controller/players/history/**").hasRole("ADMIN")
        .antMatchers(HttpMethod.GET, USER_PATTERN).hasRole("USER")
        .antMatchers(HttpMethod.POST, USER_PATTERN).hasRole("USER")
        .antMatchers(HttpMethod.PUT, USER_PATTERN).hasRole("USER")
        .antMatchers(HttpMethod.DELETE, USER_PATTERN).hasRole("USER")
        .and()
        .csrf().disable()
        .formLogin().disable();
    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return new InMemoryUserDetailsManager(
        User.withUsername("user").password(encoder.encode("password")).roles("USER").build(),
        User.withUsername("admin").password(encoder.encode("password")).roles("ADMIN", "USER").build()
    );
  }
}