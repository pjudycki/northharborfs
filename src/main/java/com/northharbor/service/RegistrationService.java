package com.northharbor.service;

import java.util.Locale;
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

		String userName = form.getUsername().trim().toLowerCase(Locale.ROOT);

		String email = form.getEmail().trim();

		if (userRepository.existsByUsernameIgnoreCase(userName)) {
			throw new DuplicateUserException("username", "The login is already used");
		}

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateUserException("email", "The email is already used");
		}

		String passwordHash = passwordEncoder.encode(form.getPassword());

		NhUserEntity user = new NhUserEntity(userName, email, passwordHash, true, Set.of(Role.USER));
		
		userRepository.save(user);

	}

}
