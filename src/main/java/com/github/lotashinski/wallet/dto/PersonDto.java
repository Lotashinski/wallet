package com.github.lotashinski.wallet.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PersonDto {

	private String email;
	
	private LocalDateTime createdAt;
	
}
