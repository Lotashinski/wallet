package com.github.lotashinski.wallet.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.RegistrationDto;
import com.github.lotashinski.wallet.exception.UsernameAlreadyExistsException;
import com.github.lotashinski.wallet.mapper.RegistrationMapperInterface;
import com.github.lotashinski.wallet.repository.PersonRepository;
import com.github.lotashinski.wallet.service.RegistrationServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService implements RegistrationServiceInterface {

	private final PersonRepository personRepository;
	
	private final RegistrationMapperInterface registrationMapper;
	
	private final PasswordEncoder encoder;
	
	
	@Override
	public void registration(RegistrationDto dto) {
		var email = dto.getEmail().toLowerCase();
		if(personRepository.findOneByEmail(email).isPresent()) {
			var text = String.format("Email \"%s\" already exists.", email);
			throw new UsernameAlreadyExistsException(text);
		}
		
		var person = registrationMapper.toEntity(dto);
		person.setCreatedAt(LocalDateTime.now());
		person.setPassword(encoder.encode(dto.getPassword()));
		
		personRepository.save(person);
	}

}
