package com.github.lotashinski.wallet.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectedWalletsDto {

	private UUID walletId;
	
	private String walletTitle;
	
	private String currency;
	
	private boolean selected;
	
}
