package com.github.lotashinski.wallet.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.github.lotashinski.wallet.entity.Sum;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WalletDto {

	private UUID id;
	
	private String title;
	
	private Collection<? extends Sum> values = new ArrayList<>();
	
	private Collection<? extends String> currencyCodes = new ArrayList<>();
	
}
