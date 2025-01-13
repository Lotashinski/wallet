package com.github.lotashinski.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.lotashinski.wallet.service.TransfersServiceInterface;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final TransfersServiceInterface transferService;
	
	
	@GetMapping({"/", "/main"})
	public String home(Model model) {
		model.addAttribute("transfers", transferService.getLast());
		
		return "index";
	}
	
}
