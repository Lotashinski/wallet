package com.github.lotashinski.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTransferDto {

	private UUID id;
	
	private BigDecimal value;
	
	private UUID categoryId;
	
	private String categoryTitle;
	
	private UUID walletId;
	
	private String walletTitle;
	
	private String walletCurrency;
	
	private String description;
	
	private LocalDateTime time = LocalDateTime.now();
	
}
