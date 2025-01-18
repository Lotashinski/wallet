package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ItemWalletDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.entity.Sum;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.CategoryMapperInterface;
import com.github.lotashinski.wallet.mapper.WalletMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;
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
	
	private final WalletMapperInterface walletMapper;
	
	private final CategoryMapperInterface categoryMapper;
	
	
	@Override
	public Collection<ItemWalletDto> getAll() {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return walletRepository.findByPerson(person)
				.stream()
				.map(walletMapper::toDto)
				.toList();
	}

	@Override
	public ItemWalletDto get(UUID id) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return walletRepository
				.findByPersonAndId(person, id)
				.map(walletMapper::toDto)
				.orElseThrow(() -> generateNotFoundException(id));
	}

	@Override
	public ItemWalletDto create(SaveWalletDto dto) {
		var wallet = walletMapper.toEntity(dto);
		wallet.setCreator(SecurityHolderAdapter.getCurrentUser());
		
		var entity = walletRepository.save(wallet);
		
		return walletMapper.toDto(entity);
	}

	@Override
	public ItemWalletDto update(UUID id, SaveWalletDto dto) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return walletRepository
				.findByPersonAndId(person, id)
				.map(e -> walletMapper.updateEntity(dto, e))
				.map(walletRepository::save)
				.map(walletMapper::toDto)
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
				.map(w -> walletMapper.toDto(w, categoryWallets.contains(w)))
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
	
}
