package com.github.lotashinski.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.lotashinski.wallet.dto.ChangePasswordDto;
import com.github.lotashinski.wallet.exception.InvalidPasswordException;
import com.github.lotashinski.wallet.service.ProfileServiceInterface;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileServiceInterface profileService;
	
	@GetMapping
	public String index(Model model) {
		model.addAttribute("person", profileService.getCurrent());
		
		return "profile";
	}
	
	@PostMapping("/change_password")
	public String changePassword(ChangePasswordDto changePassword) {
		try {
			profileService.changePassword(changePassword);
			return "redirect:/profile?passwordChangeSuccess";
		} catch (InvalidPasswordException ipe) {
			return "redirect:/profile?passwordChangeException";
		}
	}
	
}
