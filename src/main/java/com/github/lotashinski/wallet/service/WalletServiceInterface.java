package com.github.lotashinski.wallet.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.github.lotashinski.wallet.dto.ItemWalletDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;

public interface WalletServiceInterface {
	
	Collection<ItemWalletDto> getAll();
	
	
	List<SelectedWalletsDto> getCategoryWallets(UUID categoryId);
	
	List<SelectedCategoryDto> getWalletCategories(UUID walletId);
	
	
	void setCategoryWallets(UUID categoryId, Collection<UUID> walletsIds);
	
	void setWalletCategories(UUID walletId, Collection<UUID> categoriesIds);
	
	
	ItemWalletDto get(UUID id);
	
	ItemWalletDto create(SaveWalletDto dto);
	
	ItemWalletDto update(UUID id, SaveWalletDto dto);
	
	void delete(UUID id);
	
}
