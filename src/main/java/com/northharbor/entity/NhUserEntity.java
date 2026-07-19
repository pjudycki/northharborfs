package com.northharbor.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.northharbor.enums.Role;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "nh_users",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_nh_users_username", columnNames = "username"),
        @UniqueConstraint(name = "unique_nh_users_email", columnNames = "email")})
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

  protected NhUserEntity() {
    // JPA constructor
  }

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW",
      justification = "Constructor validates arguments")
  public NhUserEntity(String username, String email, String passwordHash, boolean enabled,
      Set<Role> roles) {
    
    String checkedUsername = Objects.requireNonNull(username, "username");
    Set<Role> checkedRoles = Set.copyOf(Objects.requireNonNull(roles, "roles"));
    String checkedEmail = Objects.requireNonNull(email, "email");
    String checkedPasswordHash = Objects.requireNonNull(passwordHash, "passwordHash");

    this.username = checkedUsername;
    this.email = checkedEmail;
    this.passwordHash = checkedPasswordHash;
    this.enabled = enabled;
    this.roles.addAll(checkedRoles);
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
    return Set.copyOf(roles);
  }

  public void setRoles(Set<Role> roles) {
    Set<Role> replacement = Set.copyOf(Objects.requireNonNull(roles, "roles"));
    this.roles.clear();
    this.roles.addAll(replacement);
  }

  public void addRole(Role role) {
    roles.add(Objects.requireNonNull(role, "role"));
  }

  public void removeRole(Role role) {
    roles.remove(role);
  }

}
