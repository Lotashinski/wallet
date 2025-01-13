package com.github.lotashinski.wallet.dto;

import java.util.Collection;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCategoryDto {
	
	private UUID id;
	
	private String title;
	
	private Boolean hasTransfers;
	
	private Collection<ItemWalletDto> inWallets;
	
	private Collection<ItemWalletDto> wallets;
	
}
