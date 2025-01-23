package com.github.lotashinski.wallet.dto;

import java.util.Collection;
import java.util.UUID;

import com.github.lotashinski.wallet.entity.Sum;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ItemWalletValuedDto {

	private UUID id;
	
	private String title;

	private Collection<Sum> values;
	
}
