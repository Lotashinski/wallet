package com.github.lotashinski.wallet.service;

import java.util.List;
import java.util.UUID;

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;

public interface CategoryServiceInterfate {
	
	List<? extends ItemCategoryDto> getAll();
	
	List<? extends ItemCategoryDto> getWalletCategories(UUID walletId);
	
	ItemCategoryDto get(UUID id);
	
	ItemCategoryDto create(SaveCategoryDto category);
	
	ItemCategoryDto update(UUID id, SaveCategoryDto category);
	
	void delete(UUID id);
	
}
