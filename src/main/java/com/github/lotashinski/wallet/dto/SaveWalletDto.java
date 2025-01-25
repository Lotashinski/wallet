package com.github.lotashinski.wallet.dto;

import java.util.Collection;

import lombok.Data;

@Data
public class SaveWalletDto {

	private String title;
	
	private Collection<String> currencyCodes;
	
}
