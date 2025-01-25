package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ItemTransferDto;
import com.github.lotashinski.wallet.dto.SaveTransferDto;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.entity.Wallet;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.TransferMapperInterface;
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

	
	@Override
	public ItemTransferDto create(UUID wallUuid, SaveTransferDto dto) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		Transfer entity = transferMapper.toEntity(dto);
		
		log.info("Create new transfer in wallet {} {}. User {}", wallUuid, dto, person.getId());
		Wallet wallet = walletRepository.findByPersonAndId(person, wallUuid)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", wallUuid)));
		entity.setWallet(wallet);
		
		transferRepository.save(entity);
		
		return transferMapper.toDto(entity);
	}

	@Transactional
	@Override
	public ItemTransferDto update(UUID transferId, SaveTransferDto dto) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Update transfer {}. User {}", transferId, person.getId());
		log.debug("TransferId {}, saved data {}", transferId, dto);
		
		Transfer entity = transferRepository.findByPersonAndId(person, transferId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", transferId)));
		
		transferMapper.toEntity(dto, entity);
		transferRepository.save(entity);
		
		return transferMapper.toDto(entity);
	}

	@Override
	public void delete(UUID transferId) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Delete transfer {}. User {}", transferId, person.getId());
		
		Transfer entity = transferRepository
				.findByPersonAndId(person, transferId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Transfer %s not found", transferId)));
		
		transferRepository.delete(entity);
	}

	@Transactional
	@Override
	public Page<? extends ItemTransferDto> getByWallet(UUID walletId, int pageNumber) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get wallet {} transfers. User {}", walletId, person.getId());
		Wallet wallet = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %w not found", walletId)));
		
		log.debug("Load transfers for wallet {}", walletId);
		Pageable pageable = PageRequest.of(pageNumber, 15);
		Page<? extends Transfer> page = transferRepository
				.getByWalletOrderByTimeDesc(wallet, pageable);
		
		return page.map(transferMapper::toDto);
	}

	@Override
	public ItemTransferDto get(UUID transferId) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get transfer {}. User {}", transferId, person.getId());
		
		return transferRepository
				.findByPersonAndId(person, transferId)
				.map(transferMapper::toDto)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Transfer %s not found", transferId)));
	}

	@Transactional
	@Override
	public Collection<? extends ItemTransferDto> getLast() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		log.info("Load last transactions. User {}", person.getId());
		
		return transferRepository
				.getByOrderByTimeDesc(person, Limit.of(15))
				.stream()
				.map(transferMapper::toDto)
				.toList();
	}

}
