package com.github.lotashinski.wallet.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.github.lotashinski.wallet.dto.ItemWalletDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.entity.Wallet;

@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapperInterface {

	ItemWalletDto toDto(Wallet entity);
	
	@Mapping(target = "walletId", source = "entity.id")
	@Mapping(target = "walletTitle", source = "entity.title")
	SelectedWalletsDto toDto(Wallet entity, boolean selected);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	@Mapping(target = "value", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "categories", ignore = true)
	Wallet toEntity(ItemWalletDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	@Mapping(target = "value", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "categories", ignore = true)
	Wallet toEntity(SaveWalletDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	@Mapping(target = "value", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "categories", ignore = true)
	Wallet updateEntity(SaveWalletDto dto, @MappingTarget Wallet target);
	
}
