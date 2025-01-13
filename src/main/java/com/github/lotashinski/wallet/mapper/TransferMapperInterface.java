package com.github.lotashinski.wallet.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.mapper.decorator.TransferMapperDecorator;

@org.mapstruct.DecoratedWith(TransferMapperDecorator.class)
@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransferMapperInterface {
	
	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "categoryTitle", source = "category.title")
	@Mapping(target = "walletId", source = "wallet.id")
	@Mapping(target = "walletTitle", source = "wallet.title")
	@Mapping(target = "walletCurrency", source = "wallet.currency")
	ItemTransferDto toDto(Transfer transfer);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "wallet", ignore = true)
	Transfer toEntity(ItemTransferDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "wallet", ignore = true)
	Transfer toEntity(SaveTransferDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "wallet", ignore = true)
	Transfer toEntity(SaveTransferDto dto, @MappingTarget Transfer transfer);
	
}
