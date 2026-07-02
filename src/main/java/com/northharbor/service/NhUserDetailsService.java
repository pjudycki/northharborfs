package com.northharbor.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.northharbor.entity.NhUserEntity;
import com.northharbor.repository.NhUserRepository;

@Service
@Transactional(readOnly = true)
public class NhUserDetailsService implements UserDetailsService {

	private final NhUserRepository nhUserRepository;

	public NhUserDetailsService(NhUserRepository nhUserRepository) {
		this.nhUserRepository = nhUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		NhUserEntity nhUserEntity = nhUserRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found" + username));

		List<SimpleGrantedAuthority> authorities = nhUserEntity.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();

		return User.withUsername(nhUserEntity.getUsername()).password(nhUserEntity.getPasswordHash())
				.authorities(authorities).disabled(!nhUserEntity.isEnabled()).build();
	}

}
