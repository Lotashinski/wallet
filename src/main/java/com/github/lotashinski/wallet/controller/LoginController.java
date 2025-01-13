package com.github.lotashinski.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.lotashinski.wallet.security.SecurityHolderAdapter;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {
	
	@GetMapping("/login")
	public String loginPage() {
		var current = SecurityHolderAdapter.getCurrentUser();
		if (current != null) {
			return "redirect:/";
		}
		
		return "login";
	}
	
}
