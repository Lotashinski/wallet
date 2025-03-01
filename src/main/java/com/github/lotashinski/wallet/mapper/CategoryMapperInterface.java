package com.github.lotashinski.wallet.mapper;

import java.util.Collection;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Sum;


@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WalletMapperInterface.class})
public interface CategoryMapperInterface {

	ItemCategoryDto toDto(Category category, Collection<? extends Sum> last30Days);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "wallets", ignore = true)
	@Mapping(target = "categoryWallets", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	Category toEntity(SaveCategoryDto dto);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "wallets", ignore = true)
	@Mapping(target = "categoryWallets", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	Category toEntity(SaveCategoryDto dto, Person creator);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "wallets", ignore = true)
	@Mapping(target = "categoryWallets", ignore = true)
	@Mapping(target = "transfers", ignore = true)
	Category updateEntity(SaveCategoryDto dto, @MappingTarget Category target);
	
	@Mapping(target = "categoryId", source = "entity.id")
	@Mapping(target = "categoryTitle", source = "entity.title")
	SelectedCategoryDto toDto(Category entity, boolean selected);
	
}
