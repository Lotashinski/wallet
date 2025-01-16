package com.github.lotashinski.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.lotashinski.wallet.service.CategoryServiceInterfate;
import com.github.lotashinski.wallet.service.TransfersServiceInterface;
import com.github.lotashinski.wallet.service.WalletServiceInterface;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final TransfersServiceInterface transferService;
	
	private final WalletServiceInterface walletService;
	
	private final CategoryServiceInterfate categoryService;
	
	
	@GetMapping({"/", "/main"})
	public String home(Model model) {
		model.addAttribute("transfers", transferService.getLast());
		model.addAttribute("categories", categoryService.getAll());
		model.addAttribute("wallets", walletService.getAll());
		
		return "index";
	}
	
}
