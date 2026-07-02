package com.northharbor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.northharbor.exception.DuplicateUserException;
import com.northharbor.form.RegistrationForm;
import com.northharbor.service.RegistrationService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

	private final RegistrationService registrationService;

	public AuthController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String registrationForm(Model model) {
		model.addAttribute("form", new RegistrationForm());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			form.setPassword("");
			return "register";
		}

		try {

		} catch (DuplicateUserException exception) {
			bindingResult.rejectValue(exception.getField(), "duplicate", exception.getMessage());
			form.setPassword("");
			return "register";
		}

		return "redirect:/login?registered";
	}
}
