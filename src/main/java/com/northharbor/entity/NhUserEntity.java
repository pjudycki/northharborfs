package com.northharbor.entity;

import java.util.HashSet;
import java.util.Set;

import com.northharbor.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "nh_users", uniqueConstraints = {
		@UniqueConstraint(name = "unique_nh_users_username", columnNames = "username"),
		@UniqueConstraint(name = "unique_nh_users_email", columnNames = "email") })
public class NhUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String username;

	@Column(nullable = false, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false)
	private boolean enabled = true;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "nh_users_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 30)
	private Set<Role> roles = new HashSet<>();

	public NhUserEntity(String username, String email, String passwordHash, boolean enabled, Set<Role> roles) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}
