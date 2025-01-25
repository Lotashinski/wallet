package com.github.lotashinski.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveTransferDto {
	
	@NotNull
	private BigDecimal value;
	
	@NotBlank
	private String currencyCode;
	
	@NotNull
	private UUID categoryId;
	
	private String description;
	
	@NotNull
	private LocalDateTime time = LocalDateTime.now();
	
}
