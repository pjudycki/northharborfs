package com.northharbor.service;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.northharbor.entity.NhUserEntity;
import com.northharbor.enums.Role;
import com.northharbor.exception.DuplicateUserException;
import com.northharbor.form.RegistrationForm;
import com.northharbor.repository.NhUserRepository;

@Service
public class RegistrationService {

  private final NhUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationService(NhUserRepository userRepository, PasswordEncoder passwordEncoder) {
    super();
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void register(RegistrationForm form) {

    Objects.requireNonNull(form, "Registration form must not be null");

    String userName = requireText(form.getUsername(), "username").trim().toLowerCase(Locale.ROOT);

    String email = requireText(form.getEmail(), "email").trim();

    String rawPassword = requireText(form.getPassword(), "password").trim();

    if (userRepository.existsByUsernameIgnoreCase(userName)) {
      throw new DuplicateUserException("username", "The login is already used");
    }

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new DuplicateUserException("email", "The email is already used");
    }

    String passwordHash = Objects.requireNonNull(passwordEncoder.encode(rawPassword),
        "Password encoder returned null for a non-null password");

    NhUserEntity user = new NhUserEntity(userName, email, passwordHash, true, Set.of(Role.USER));

    userRepository.save(user);

  }

  private static String requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }

}
