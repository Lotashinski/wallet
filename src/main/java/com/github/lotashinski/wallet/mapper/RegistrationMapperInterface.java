package com.github.lotashinski.wallet.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.github.lotashinski.wallet.dto.RegistrationDto;
import com.github.lotashinski.wallet.entity.Person;

@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegistrationMapperInterface {
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Person toEntity(RegistrationDto registration);
	
}
