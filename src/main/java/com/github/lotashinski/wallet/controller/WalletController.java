package com.github.lotashinski.wallet.controller;

import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.WalletDto;
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
		model.addAttribute("sum", walletService.getSumForAllWallets());
		
		return "wallets";
	}
	
	@GetMapping("/new")
	public String newWalletPage(Model model) {
		model.addAttribute("wallet", new WalletDto());
		
		return "wallet_form";
	}
	
	@PostMapping("/new")
	public String newWallet(Model model, SaveWalletDto wallet) {
		var item = walletService.create(wallet);
		
		return "redirect:/wallets/" + item.getId();
	}
	
	@GetMapping("/{id}")
	public String editWalletPage(Model model, @PathVariable UUID id) {
		model.addAttribute("wallet", walletService.get(id));
		model.addAttribute("categories", walletService.getWalletCategories(id));
		
		return "wallet_form";
	}
	
	@RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editWallet(@PathVariable UUID id, SaveWalletDto wallet) {
		var item = walletService.update(id, wallet);
		
		return "redirect:/wallets/" + item.getId();
	}
	
	@PostMapping(path = "/{id}/categories")
	public String editWalletCategories(@PathVariable UUID id, @RequestParam(required = false) Collection<UUID> selected) {
		walletService.setWalletCategories(id, selected);
		
		return "redirect:/wallets/" + id;
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable UUID id) {
		walletService.delete(id);
		
		return "redirect:/wallets";
	}
	
}
