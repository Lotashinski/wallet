package com.github.lotashinski.wallet.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.github.lotashinski.wallet.dto.ItemWalletDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;

public interface WalletServiceInterface {
	
	Collection<ItemWalletDto> getAll();
	
	List<SelectedWalletsDto> getCategoryWallets(UUID categoryid);
	
	void setCategoryForWallets(UUID categoryId, Collection<UUID> walletsIds);
	
	ItemWalletDto get(UUID id);
	
	ItemWalletDto create(SaveWalletDto dto);
	
	ItemWalletDto update(UUID id, SaveWalletDto dto);
	
	void delete(UUID id);
	
}
