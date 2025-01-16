package com.github.lotashinski.wallet.service;

import com.github.lotashinski.wallet.dto.ChangePasswordDto;
import com.github.lotashinski.wallet.dto.PersonDto;
import com.github.lotashinski.wallet.exception.InvalidPasswordException;

public interface ProfileServiceInterface {
	
	PersonDto getCurrent();
	
	void changePassword(ChangePasswordDto changePassword) throws InvalidPasswordException;
	
}
