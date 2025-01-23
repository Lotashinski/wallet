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
import org.springframework.web.bind.annotation.RequestParam;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;
import com.github.lotashinski.wallet.service.CategoryServiceInterfate;
import com.github.lotashinski.wallet.service.TransfersServiceInterface;
import com.github.lotashinski.wallet.service.WalletServiceInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

	private final TransfersServiceInterface transferService;

	private final CategoryServiceInterfate categoryService;
	
	private final WalletServiceInterface walletService;

	
	@GetMapping
	public String index(@RequestParam UUID walletId, Model model) {
		model.addAttribute("wallet", walletService.get(walletId));
		model.addAttribute("transfers", transferService.getByWallet(walletId));

		return "transfers";
	}

	
	@GetMapping("/new")
	public String createPage(@RequestParam UUID walletId, Model model) {
		model.addAttribute("transfer", new ItemTransferDto());
		model.addAttribute("categories", categoryService.getWalletCategories(walletId));
		model.addAttribute("currencies", walletService.get(walletId).getCurrencyCodes());
		
		return "transfer_form";
	}
	
	@PostMapping("/new")
	public String create(@RequestParam UUID walletId, @Valid SaveTransferDto transfer) {
		transferService.create(walletId, transfer);
		
		return "redirect:/transfers?walletId=" + walletId;
	}

	@GetMapping("/{id}")
	public String editPage(@PathVariable("id") UUID transferId, Model model) {
		ItemTransferDto transfer = transferService.get(transferId);
		
		model.addAttribute("transfer", transfer);
		model.addAttribute("categories", categoryService.getWalletCategories(transfer.getWalletId()));
		model.addAttribute("currencies", walletService.get(transfer.getWalletId()).getCurrencyCodes());
		
		return "transfer_form";
	}
	
	@RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String edit(@PathVariable("id") UUID transferId, @Valid SaveTransferDto transfer) {
		ItemTransferDto dto = transferService.update(transferId, transfer);
		
		return "redirect:/transfers?walletId=" + dto.getWalletId();
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") UUID transferId) {
		var dto = transferService.get(transferId);
		transferService.delete(transferId);
		
		return "redirect:/transfers?walletId=" + dto.getWalletId();
	}
	
}
