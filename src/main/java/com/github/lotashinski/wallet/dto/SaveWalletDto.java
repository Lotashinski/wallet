package com.github.lotashinski.wallet.dto;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveWalletDto {

	private String title;
	
	private Collection<String> currencyCodes;
	
}
