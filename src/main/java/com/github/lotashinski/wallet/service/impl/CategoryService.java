package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;
import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.entity.Wallet;
import com.github.lotashinski.wallet.exception.NotFoundHttpException;
import com.github.lotashinski.wallet.mapper.CategoryMapperInterface;
import com.github.lotashinski.wallet.repository.CategoryRepository;
import com.github.lotashinski.wallet.repository.TransferRepository;
import com.github.lotashinski.wallet.repository.WalletRepository;
import com.github.lotashinski.wallet.security.SecurityHolderAdapter;
import com.github.lotashinski.wallet.service.CategoryServiceInterfate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService implements CategoryServiceInterfate {
	
	private final CategoryRepository categoryRepository;
	
	private final WalletRepository walletRepository;
	
	private final TransferRepository transferRepository;
	
	private final CategoryMapperInterface transferCategoryMapper;
	
	
	@Override
	public ItemCategoryDto get(UUID id) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return categoryRepository
				.findByPersonAndId(person, id)
				.map(c -> transferCategoryMapper.toDto(c, findByCategoryInTransfers(c)))
				.orElseThrow(() -> new NotFoundHttpException(String.format("Category(%s) not found", id.toString())));
	}

	@Transactional
	@Override
	public ItemCategoryDto create(SaveCategoryDto dto) {
		var entity = transferCategoryMapper.toEntity(dto);
	
		entity.setCreator(SecurityHolderAdapter.getCurrentUser());
		categoryRepository.save(entity);

		return transferCategoryMapper.toDto(entity, List.of());
	}

	@Transactional
	@Override
	public ItemCategoryDto update(UUID id, SaveCategoryDto category) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		return categoryRepository
				.findByPersonAndId(person, id)
				.map(e -> transferCategoryMapper.updateEntity(category, e))
				.map(categoryRepository::save)
				.map(c -> transferCategoryMapper.toDto(c, findByCategoryInTransfers(c)))
				.orElseThrow(() -> new NotFoundHttpException(String.format("Category(%s) not found", id.toString())));
	}

	@Transactional
	@Override
	public void delete(UUID id) {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		var entity = categoryRepository
				.findByPersonAndId(person, id)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Category(%s) not found", id.toString())));
		
		entity.getTransfers()
			.forEach(t -> t.setCategory(null));
		
		categoryRepository.delete(entity);
	}

	@Override
	public List<ItemCategoryDto> getAll() {
		var person = SecurityHolderAdapter.getCurrentUser();
		
		var categories = categoryRepository
				.findByPerson(person);
		var walletMap = findByCategoriesInTransfers(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, walletMap.get(e)))
				.toList();
	}
	
	@Override
	public List<ItemCategoryDto> getWalletCategories(UUID walletId) {
		var person = SecurityHolderAdapter.getCurrentUser();
		var wallet = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", walletId)));
		
		var categories = categoryRepository
				.findByPersonAndWallet(person, wallet);
		var walletMap = findByCategoriesInTransfers(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, walletMap.get(e)))
				.toList();
	}
		
	private Collection<Wallet> findByCategoryInTransfers(Category c) {
		return findByCategoriesInTransfers(List.of(c)).get(c);
	}
	
	private	Map<Category, ? extends Collection<Wallet>> findByCategoriesInTransfers(
			Collection<? extends Category> categories) {
		
		return transferRepository.findByCategories(categories)
			.stream()
			.collect(HashMap<Category, Set<Wallet>>::new, CategoryService::consumeTransfer, Map::putAll);
	}
	
	private static Map<Category, Set<Wallet>> consumeTransfer(
			Map<Category, Set<Wallet>> map, Transfer t) {
		
		var category = t.getCategory();
		var set = map.get(category);
		
		if (set == null) {
			set = new HashSet<Wallet>();
			map.put(category, set);
		}
		
		set.add(t.getWallet());
		
		return map;
	}
	
}
