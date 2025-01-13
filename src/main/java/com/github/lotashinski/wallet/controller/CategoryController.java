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

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;
import com.github.lotashinski.wallet.service.CategoryServiceInterfate;
import com.github.lotashinski.wallet.service.WalletServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryServiceInterfate transferCategoryService;
	
	private final WalletServiceInterface walletService;
	
	@GetMapping
	public String index(Model model) {
		model.addAttribute("categories", transferCategoryService.getAll());
		
		return "categories";
	}
	
	@PostMapping("/new")
	public String newCategory(SaveCategoryDto category) {
		transferCategoryService.create(category);
		
		return "redirect:/categories";
	}
	
	@GetMapping("/new")
	public String newCategoryPage(Model model) {
		model.addAttribute("category", new ItemCategoryDto());
		
		return "categories_form";
	}
	
	@GetMapping("/{id}")
	public String editPage(Model model, @PathVariable UUID id) {
		model.addAttribute("category", transferCategoryService.get(id));
		model.addAttribute("wallets", walletService.getCategoryWallets(id));
		
		return "categories_form";
	}
	
	@RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String edit(@PathVariable UUID id, SaveCategoryDto category) {
		transferCategoryService.update(id, category);
		
		return "redirect:/categories/" + id;
	}
	
	@PostMapping(path = "/{id}/wallets")
	public String editWallets(@PathVariable UUID id, @RequestParam Collection<UUID> selected) {
		walletService.setCategoryForWallets(id, selected);
		
		return "redirect:/categories/" + id;
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable UUID id) {
		transferCategoryService.delete(id);
		
		return "redirect:/categories";
	}
	
}
