package com.northharbor.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

	@NotBlank(message = "Enter username")
	@Size(min = 3, max = 50, message = "Login must have from 3 to 50 characters")
	private String username;

	@NotBlank(message = "Enter password")
	@Size(min = 12, max = 72, message  = "Password must have from 12 to 72 characters")
	private String password;
	
	@NotBlank(message = "Enter email")
	@Email(message = "Enter correct email address")
	@Size(max = 254)
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
