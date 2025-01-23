package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.TransferMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;
import com.github.lotashinski.wallet.repository.TransferRepository;
import com.github.lotashinski.wallet.repository.WalletRepository;
import com.github.lotashinski.wallet.security.SecurityHolderAdapter;
import com.github.lotashinski.wallet.service.TransfersServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService implements TransfersServiceInterface {

	private final TransferRepository transferRepository;
	
	private final WalletRepository walletRepository;
	
	private final TransferMapperInterface transferMapper;
	
	private final CategoryRepository categoryRepository;

	
	@Override
	public ItemTransferDto create(UUID wallUuid, SaveTransferDto dto) {
		var entity = transferMapper.toEntity(dto);
		var person = SecurityHolderAdapter.getCurrentUser();
		var wallet = walletRepository.findByPersonAndId(person, wallUuid)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", wallUuid)));
		entity.setWallet(wallet);
		
		if (dto.getCategoryId() != null) {
			var category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new NotFoundHttpException(String.format("Category %s not found", dto.getCategoryId())));
			entity.setCategory(category);
		}
		
		transferRepository.save(entity);
		
		return transferMapper.toDto(entity);
	}

	@Transactional
	@Override
	public ItemTransferDto update(UUID transferId, SaveTransferDto dto) {
		var person = SecurityHolderAdapter.getCurrentUser();
		var entity = transferRepository.findByPersonAndId(person, transferId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", transferId)));
		
		transferMapper.toEntity(dto, entity);
		transferRepository.save(entity);
		
		return transferMapper.toDto(entity);
	}

	@Override
	public void delete(UUID transferId) {
		var entity = transferRepository.findById(transferId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", transferId)));
		
		transferRepository.delete(entity);
	}

	@Transactional
	@Override
	public Collection<ItemTransferDto> getByWallet(UUID walletId) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		var wallet = walletRepository.findByPersonAndId(person, walletId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %w not found", walletId)));
		
		return transferRepository
				.getByWalletOrderByTimeDesc(wallet)
				.stream()
				.map(transferMapper::toDto)
				.toList();
	}

	@Override
	public ItemTransferDto get(UUID transferId) {
		
		return transferRepository.findById(transferId)
				.map(transferMapper::toDto)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Transfer %s not found", transferId)));
	}

	@Transactional
	@Override
	public Collection<ItemTransferDto> getLast() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		log.info("Load last transactions for person {}", person.getId());
		
		return transferRepository
				.getByOrderByTimeDesc(person, Limit.of(15))
				.stream()
				.map(transferMapper::toDto)
				.toList();
	}

}
