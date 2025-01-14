package com.github.lotashinski.wallet.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectedCategoryDto {

	private UUID categoryId;
	
	private String categoryTitle;
	
	private boolean selected;
	
}
