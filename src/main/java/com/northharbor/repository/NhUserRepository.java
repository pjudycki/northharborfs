package com.northharbor.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.northharbor.entity.NhUserEntity;

public interface NhUserRepository extends JpaRepository<NhUserEntity, Long> {
	
	Optional<NhUserEntity> findByUsernameIgnoreCase(String username);
	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByEmailIgnoreCase(String email);
	

}
