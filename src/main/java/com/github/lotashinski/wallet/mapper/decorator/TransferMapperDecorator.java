package com.github.lotashinski.wallet.mapper.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.TransferMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;

import lombok.Setter;

public abstract class TransferMapperDecorator implements TransferMapperInterface {

	@Setter
	@Autowired
	@Qualifier("delegate")
	private TransferMapperInterface delegate;

	@Setter
	@Autowired
	private CategoryRepository categoryRepository;

	
	@Override
	public Transfer toEntity(ItemTransferDto dto) {
		var entity = delegate.toEntity(dto);
		var category = dto.getCategoryId() != null
				?	categoryRepository
						.findById(dto.getCategoryId())
						.orElseThrow(() -> new NotFoundHttpException(String.format("Category %s not found", dto.getCategoryId())))
				: null;

		entity.setCategory(category);
		
		return entity;
	}

}
