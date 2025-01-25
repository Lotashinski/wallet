package com.github.lotashinski.wallet.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ItemWalletValuedDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.dto.WalletDto;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Sum;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.entity.Wallet;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.CategoryMapperInterface;
import com.github.lotashinski.wallet.mapper.WalletMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;
import com.github.lotashinski.wallet.repository.TransferRepository;
import com.github.lotashinski.wallet.repository.WalletRepository;
import com.github.lotashinski.wallet.security.SecurityHolderAdapter;
import com.github.lotashinski.wallet.service.WalletServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService implements WalletServiceInterface {

	private final WalletRepository walletRepository;
	
	private final CategoryRepository categoryRepository;
	
	private final TransferRepository transferRepository;
	
	private final WalletMapperInterface walletMapper;
	
	private final CategoryMapperInterface categoryMapper;
	
	
	@Override
	public Collection<ItemWalletValuedDto> getAll() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Load wallets for user {}", person.getId());
		Collection<Wallet> wallets = walletRepository.findByPerson(person);
		
		log.debug("Load transfers for wallets", person.getId());
		Map<UUID, List<Transfer>> transfers = transferRepository
				.getByWalletOrderByTimeDesc(wallets)
				.stream()
				.collect(Collectors.groupingBy(t -> t.getWallet().getId()));
		
		log.debug("Map wallets", person.getId());
		return wallets
				.stream()
				.map(w -> walletMapper.toItemDto(w, calculateSum(transfers.get(w.getId()))))
				.toList();
	}

	@Override
	public WalletDto get(UUID id) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		Wallet wallet = walletRepository.findByPersonAndId(person, id)
				.orElseThrow(() -> generateNotFoundException(id));
		Collection<Transfer> transfers = transferRepository
				.getByWalletOrderByTimeDesc(wallet);
		
		return walletMapper.toDto(wallet, calculateSum(transfers));
	}

	@Override
	public WalletDto create(SaveWalletDto dto) {
		var wallet = walletMapper.toEntity(dto);
		wallet.setCreator(SecurityHolderAdapter.getCurrentUser());
		
		var entity = walletRepository.save(wallet);
		
		return walletMapper.toDto(entity, List.of());
	}

	@Override
	public WalletDto update(UUID id, SaveWalletDto dto) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return walletRepository
				.findByPersonAndId(person, id)
				.map(e -> walletMapper.updateEntity(dto, e))
				.map(walletRepository::save)
				.map(w -> {
					Collection<Transfer> t = transferRepository
							.getByWalletOrderByTimeDesc(w);
					
					return walletMapper.toDto(w, calculateSum(t));
				})
				.orElseThrow(() -> WalletService.generateNotFoundException(id));
	}

	@Override
	public void delete(UUID id) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		var entity = walletRepository
				.findByPersonAndId(person, id)
				.orElseThrow(() -> generateNotFoundException(id));
		
		walletRepository.delete(entity);
	}
	
	private static RuntimeException generateNotFoundException(UUID id) {
		return new NotFoundHttpException(String.format("Wallet %s not found.", id.toString()));
	}

	@Override
	public List<SelectedWalletsDto> getCategoryWallets(UUID categoryid) {
		var person = SecurityHolderAdapter.getCurrentUser();
		var category = categoryRepository
				.findByPersonAndId(person, categoryid)
				.orElseThrow(() -> categoryNotFoundException(categoryid));
		
		var allWallets = walletRepository.findByPerson(person);
		var categoryWallets = new HashSet<>(walletRepository.findByPersonAndCategory(person, category));
		
		return allWallets
				.stream()
				.map(w -> walletMapper.toSelectedDto(w, categoryWallets.contains(w)))
				.toList();
	}

	@Override
	public void setCategoryWallets(UUID categoryId, Collection<UUID> walletsIds) {
		var person = SecurityHolderAdapter.getCurrentUser();
	
		var category = categoryRepository.findByPersonAndId(person, categoryId)
				.orElseThrow(() -> categoryNotFoundException(categoryId));
		
		var newState = walletRepository.findByPersonAndIds(person, walletsIds);
		var oldState = category.getWallets();
	
	
		var forPersists = new HashSet<>(newState);
		forPersists.removeAll(oldState);
		
		var forDelete = new HashSet<>(oldState);
		forDelete.removeAll(newState);
		
		forDelete.stream()
			.forEach(category::removeWallet);
		
		forPersists.stream()
			.forEach(category::addWallet);
		
		categoryRepository.save(category);
	}
	
	private static RuntimeException categoryNotFoundException(UUID categoryId) {
		return new NotFoundHttpException(String.format("Category %s not found", categoryId));
	}

	@Override
	public List<SelectedCategoryDto> getWalletCategories(UUID walletId) {
		var person = SecurityHolderAdapter.getCurrentUser();
		var entity = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> generateNotFoundException(walletId));
		
		var allCategories = categoryRepository.findByPerson(person);
		var walletCategories = new HashSet<>(categoryRepository.findByPersonAndWallet(person, entity));
		
		return allCategories
				.stream()
				.map(e -> categoryMapper.toDto(e, walletCategories.contains(e)))
				.toList();
	}

	@Override
	public void setWalletCategories(UUID walletId, Collection<UUID> categoriesIds) {
		var person = SecurityHolderAdapter.getCurrentUser();
		var entity = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> generateNotFoundException(walletId));
		
		var oldState = categoryRepository.findByPersonAndWallet(person, entity);
		var newState = categoryRepository.findByPersonAndIds(person, categoriesIds);
		
		var forPersists = new HashSet<>(newState);
		forPersists.removeAll(oldState);
		
		var forDelete = new HashSet<>(oldState);
		forDelete.removeAll(newState);
		
		forDelete.stream()
			.forEach(entity::removeCategory);
	
		forPersists.stream()
			.forEach(entity::addCategory);
		
		walletRepository.save(entity);
	}

	@Override
	public Collection<Sum> getSumForAllWallets() {
		return walletRepository.getSumForPerson(SecurityHolderAdapter.getCurrentUser());
	}
	
	private static Collection<Sum> calculateSum(Collection<Transfer> transfers) {
		Map<String, List<Transfer>> grouping = transfers
			.stream()
			.collect(Collectors.groupingBy(Transfer::getCurrencyCode));
		
		return grouping.entrySet()
				.stream()
				.map(e -> {
				     BigDecimal sum = e.getValue()
				    		 .stream()
				    		 .map(Transfer::getValue)
				    		 .reduce((l, r) -> l.add(r))
				    		 .orElse(BigDecimal.ZERO);
				     
				     return new Sum(e.getKey(), sum);
				})
				.toList();
	}
	
}
