package com.github.lotashinski.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ItemWalletDto {

	private UUID id;
	
	private String title;
	
	private String currency;
	
	private BigDecimal value;
	
}
