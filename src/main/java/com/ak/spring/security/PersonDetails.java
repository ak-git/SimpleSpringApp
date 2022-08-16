package com.ak.spring.security;

import java.util.Collection;
import java.util.List;

import com.ak.spring.data.entity.Person;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class PersonDetails implements UserDetails {
  private final String userName;
  private final String password;
  private final boolean enabled;
  private final List<GrantedAuthority> authorities;

  public PersonDetails(@NonNull Person person) {
    userName = person.getName();
    password = person.getPassword();
    enabled = person.isActive();
    authorities = List.of(new SimpleGrantedAuthority(person.getRole().name()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
