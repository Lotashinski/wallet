package com.github.lotashinski.wallet.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Sum {

	private String currency;
	
	private BigDecimal value;
	
}
