package com.github.lotashinski.wallet.dto;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Page<T> {
	
	private Collection<T> elements;
	
	private int totalSize;
	
	private int pageSize;
	
	private int pageNumber;
	
}
