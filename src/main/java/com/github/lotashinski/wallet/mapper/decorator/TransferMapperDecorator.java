package com.github.lotashinski.wallet.mapper.decorator;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;
import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.TransferMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;
import com.github.lotashinski.wallet.security.SecurityHolderAdapter;

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
		Transfer entity = delegate.toEntity(dto);
		
		return setCategory(entity, dto.getCategoryId());
	}

	@Override
	public Transfer toEntity(SaveTransferDto dto) {
		Transfer entity = delegate.toEntity(dto);
		
		return setCategory(entity, dto.getCategoryId());
	}
	
	@Override
	public Transfer toEntity(SaveTransferDto dto, Transfer transfer) {
		Transfer entity = delegate.toEntity(dto, transfer);
		
		return setCategory(entity, dto.getCategoryId());
	}
	
	private Transfer setCategory(Transfer transfer, UUID categgoryId) {
		if (categgoryId != null) {
			Category category = categoryRepository
				.findByPersonAndId(SecurityHolderAdapter.getCurrentUser(), categgoryId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Categgory %s not found", categgoryId)));
			transfer.setCategory(category);
		} else {
			transfer.setCategory(null);
		}
		
		return transfer;
	}
	
}
