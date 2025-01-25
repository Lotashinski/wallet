package com.github.lotashinski.wallet.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ChangePasswordDto;
import com.github.lotashinski.wallet.dto.PersonDto;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.exception.InvalidPasswordException;
import com.github.lotashinski.wallet.mapper.PersonMapperInterface;
import com.github.lotashinski.wallet.repository.PersonRepository;
import com.github.lotashinski.wallet.security.SecurityHolderAdapter;
import com.github.lotashinski.wallet.service.ProfileServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileServiceInterface {

	private final PersonRepository personRepository;
	
	private final PersonMapperInterface personMapper;
	
	private final PasswordEncoder encoder;
	
	
	
	@Override
	public PersonDto getCurrent() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		return personMapper.toDto(person);
	}


	@Override
	public void changePassword(ChangePasswordDto changePassword) throws InvalidPasswordException {
		Person person = SecurityHolderAdapter.getCurrentUser();
		String currentPassword = person.getPassword();
		
		if (! encoder.matches(changePassword.getCurrentPassword(), currentPassword)) {
			throw new InvalidPasswordException();
		}
		
		person.setPassword(encoder.encode(changePassword.getCurrentPassword()));
		personRepository.save(person);
	}

}
