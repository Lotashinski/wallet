package com.github.lotashinski.wallet.mapper;

import java.util.Collection;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.github.lotashinski.wallet.dto.ItemWalletValuedDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.dto.WalletDto;
import com.github.lotashinski.wallet.entity.Sum;
import com.github.lotashinski.wallet.entity.Wallet;
import com.github.lotashinski.wallet.mapper.decorator.WalletMapperDecorator;

@org.mapstruct.DecoratedWith(WalletMapperDecorator.class)
@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapperInterface {

	WalletDto toDto(Wallet wallet, Collection<Sum> values);
	
	ItemWalletValuedDto toItemDto(Wallet entity, Collection<Sum> values);
    
	@Mapping(target = "walletId", source = "entity.id")
	@Mapping(target = "walletTitle", source = "entity.title")
	SelectedWalletsDto toSelectedDto(Wallet entity, boolean selected);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "categories", ignore = true)
	@Mapping(target = "categoryWallets", ignore = true)
	@Mapping(target = "currencies", ignore = true)
	@Mapping(target = "currencyCodes", ignore = true)
	Wallet toEntity(SaveWalletDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "categories", ignore = true)
	@Mapping(target = "categoryWallets", ignore = true)
	@Mapping(target = "currencies", ignore = true)
	@Mapping(target = "currencyCodes", ignore = true)
	Wallet updateEntity(SaveWalletDto dto, @MappingTarget Wallet target);
	
}
