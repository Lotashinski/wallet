package com.github.lotashinski.wallet.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.github.lotashinski.wallet.dto.ItemWalletValuedDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.dto.WalletDto;
import com.github.lotashinski.wallet.entity.Sum;

public interface WalletServiceInterface {
	
	Collection<? extends ItemWalletValuedDto> getAll();
	
	Collection<Sum> getSumForAllWallets();
	
	List<? extends SelectedWalletsDto> getCategoryWallets(UUID categoryId);
	
	List<? extends SelectedCategoryDto> getWalletCategories(UUID walletId);
	
	
	void setCategoryWallets(UUID categoryId, Collection<UUID> walletsIds);
	
	void setWalletCategories(UUID walletId, Collection<UUID> categoriesIds);
	
	
	WalletDto get(UUID id);
	
	WalletDto create(SaveWalletDto dto);
	
	WalletDto update(UUID id, SaveWalletDto dto);
	
	
	void delete(UUID id);
	
}
