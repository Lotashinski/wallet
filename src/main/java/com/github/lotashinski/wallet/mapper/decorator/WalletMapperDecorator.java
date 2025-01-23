package com.github.lotashinski.wallet.mapper.decorator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.entity.Wallet;
import com.github.lotashinski.wallet.mapper.WalletMapperInterface;

import lombok.Setter;

public abstract class WalletMapperDecorator implements WalletMapperInterface {

	@Setter
	@Autowired
	@Qualifier("delegate")
	private WalletMapperInterface delegate;

	@Override
	public Wallet toEntity(SaveWalletDto dto) {
		Wallet wallet = delegate.toEntity(dto);
		
		if (dto.getCurrencyCodes() == null) {
			wallet.setCurrencyCodes(List.of());
		} else {
			wallet.setCurrencyCodes(dto.getCurrencyCodes());
		}
		
		return wallet;
	}
	
	@Override
	public Wallet updateEntity(SaveWalletDto dto, Wallet target) {
		Wallet wallet = delegate.updateEntity(dto, target);
		
		if (dto.getCurrencyCodes() == null) {
			wallet.setCurrencyCodes(List.of());
		} else {
			wallet.setCurrencyCodes(dto.getCurrencyCodes());
		}
		
		return wallet;
	}
	
}
