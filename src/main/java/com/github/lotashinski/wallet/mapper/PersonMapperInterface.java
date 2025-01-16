package com.github.lotashinski.wallet.mapper;

import org.mapstruct.MappingConstants;

import com.github.lotashinski.wallet.dto.PersonDto;
import com.github.lotashinski.wallet.entity.Person;

@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapperInterface {

	PersonDto toDto(Person person);
	
}
