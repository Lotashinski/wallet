package com.github.lotashinski.wallet.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDto {

	private String email;
	
	private LocalDateTime createdAt;
	
}
