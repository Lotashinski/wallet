package com.github.lotashinski.wallet.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
	
	private final PersonRepository personRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Try login %s", username);
		
		return personRepository.findOneByEmail(username)
				.map(PersonToUserAdapter::new)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
	}

}
