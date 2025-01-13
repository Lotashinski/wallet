package com.github.lotashinski.wallet.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveWalletDto {

	private String title;
	
	private String currency;
	
	private BigDecimal value;
}
