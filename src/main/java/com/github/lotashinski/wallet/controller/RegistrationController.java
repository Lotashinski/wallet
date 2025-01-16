package com.github.lotashinski.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.github.lotashinski.wallet.dto.RegistrationDto;
import com.github.lotashinski.wallet.service.RegistrationServiceInterface;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

	private final RegistrationServiceInterface registrationService;
	
	
	@GetMapping("/registration") 
	public String registrationPage() {
		return "registration";
	}
	
	@PostMapping("/registration") 
	public String registration(RegistrationDto dto) {
		try {
		 registrationService.registration(dto);
		} catch (RuntimeException e) {
			return "redirect:/registration?error&errorText=" + e.getMessage();
		}
		
		return "redirect:/login?registrationComplete";
	}
	
}
