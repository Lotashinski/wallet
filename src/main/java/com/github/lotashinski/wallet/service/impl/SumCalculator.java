package com.github.lotashinski.wallet.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.lotashinski.wallet.entity.Sum;
import com.github.lotashinski.wallet.entity.Transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class SumCalculator {

	private Collection<? extends Transfer> transfers;
	
	
	public Collection<? extends Sum> calculate() {
		Map<String, List<Transfer>> grouping = transfers
			.stream()
			.collect(Collectors.groupingBy(Transfer::getCurrencyCode));
		
		return grouping.entrySet()
				.stream()
				.map(e -> {
				     BigDecimal sum = e.getValue()
				    		 .stream()
				    		 .map(Transfer::getValue)
				    		 .reduce((l, r) -> l.add(r))
				    		 .orElse(BigDecimal.ZERO);
				     
				     return new Sum(e.getKey(), sum);
				})
				.toList();
	}
	
}
