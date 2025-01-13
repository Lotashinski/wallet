package com.github.lotashinski.wallet.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.lotashinski.wallet.dto.ItemWalletDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.service.WalletServiceInterface;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
	
	private final WalletServiceInterface walletService;
	
	
	@GetMapping
	public String index(Model model) {
		model.addAttribute("wallets", walletService.getAll());
		
		return "wallets";
	}
	
	@GetMapping("/new")
	public String newWalletPage(Model model) {
		model.addAttribute("wallet", new ItemWalletDto());
		
		return "wallet_form";
	}
	
	@PostMapping("/new")
	public String newWallet(Model model, SaveWalletDto wallet) {
		walletService.create(wallet);
		
		return "redirect:/wallets";
	}
	
	@GetMapping("/{id}")
	public String editWalletPage(Model model, @PathVariable UUID id) {
		model.addAttribute("wallet", walletService.get(id));
		
		return "wallet_form";
	}
	
	@RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editWallet(@PathVariable UUID id, SaveWalletDto wallet) {
		walletService.update(id, wallet);
		
		return "redirect:/wallets";
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable UUID id) {
		walletService.delete(id);
		
		return "redirect:/wallets";
	}
	
}
