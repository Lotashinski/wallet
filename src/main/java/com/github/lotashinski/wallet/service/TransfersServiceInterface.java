package com.github.lotashinski.wallet.service;

import java.util.Collection;
import java.util.UUID;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;

public interface TransfersServiceInterface {

	Collection<ItemTransferDto> getByWallet(UUID walletId);
	
	Collection<ItemTransferDto> getLast();

	
	ItemTransferDto get(UUID transferId);
	
	ItemTransferDto create(UUID walletId, SaveTransferDto dto);
	
	ItemTransferDto update(UUID transferId, SaveTransferDto dto);
	
	void delete(UUID transferId);
	
}
