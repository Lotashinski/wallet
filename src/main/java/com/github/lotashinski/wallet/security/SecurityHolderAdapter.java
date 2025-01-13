package com.github.lotashinski.wallet.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.github.lotashinski.wallet.entity.Person;

public class SecurityHolderAdapter {
	
	public static Person getCurrentUser() {
		var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (user instanceof PersonToUserAdapter) {
			return ((PersonToUserAdapter) user).getPerson();
		}
		
		return null;
	}
	
}
