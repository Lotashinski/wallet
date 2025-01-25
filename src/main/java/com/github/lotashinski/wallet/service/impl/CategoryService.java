package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;
import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.CategoryWallet;
import com.github.lotashinski.wallet.entity.Person;
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
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get category {}. User {}", id, person.getId());
		return categoryRepository
				.findByPersonAndId(person, id)
				.map(c -> transferCategoryMapper.toDto(c, findByCategoryInTransfers(c)))
				.orElseThrow(() -> new NotFoundHttpException(String.format("Category(%s) not found", id.toString())));
	}

	@Transactional
	@Override
	public ItemCategoryDto create(SaveCategoryDto dto) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Create category {}. User {}", dto, person.getId());
		Category entity = transferCategoryMapper.toEntity(dto, person);

		categoryRepository.save(entity);

		return transferCategoryMapper.toDto(entity, List.of());
	}

	@Transactional
	@Override
	public ItemCategoryDto update(UUID id, SaveCategoryDto category) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		log.info("Update category {} {}. User {}", id, category, person.getId());
		
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
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Delete category {}. User {}", id, person.getId());
		
		Category entity = categoryRepository
				.findByPersonAndId(person, id)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Category(%s) not found", id.toString())));
		
		entity.getTransfers()
			.forEach(t -> t.setCategory(null));
		
		categoryRepository.delete(entity);
	}

	@Override
	public List<ItemCategoryDto> getAll() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get categories. User {}", person.getId());
		Collection<? extends Category> categories = categoryRepository
				.findByPerson(person);
		Map<Category, ? extends Collection<Wallet>> useWalletMap = findByCategoriesInTransfers(categories);
		Map<Category, ? extends Collection<Wallet>> linkWalletMap = findByLinkedCategories(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, useWalletMap.get(e), linkWalletMap.get(e)))
				.toList();
	}
	
	@Override
	public List<ItemCategoryDto> getWalletCategories(UUID walletId) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get wallet {} categories. User {}", walletId, person.getId());
		Wallet wallet = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> new NotFoundHttpException(String.format("Wallet %s not found", walletId)));
		
		Collection<? extends Category> categories = categoryRepository
				.findByPersonAndWallet(person, wallet);
		Map<Category, ? extends Collection<Wallet>> walletMap = findByCategoriesInTransfers(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, walletMap.get(e)))
				.toList();
	}
		
	private Collection<Wallet> findByCategoryInTransfers(Category c) {
		return findByCategoriesInTransfers(List.of(c)).get(c);
	}
	
	private Map<Category, ? extends Collection<Wallet>> findByLinkedCategories(
			Collection<? extends Category> categories) {
		
		return walletRepository
				.findByCategories(categories)
				.stream()
				.collect(Collectors.groupingBy(CategoryWallet::getCategory, 
							Collectors.mapping(CategoryWallet::getWallet, 
										Collectors.toSet()
								)
							)
						);
	}
	
	private	Map<Category, ? extends Collection<Wallet>> findByCategoriesInTransfers(
			Collection<? extends Category> categories) {
		
		return transferRepository
			.findByCategories(categories)
			.stream()
			.collect(HashMap<Category, Set<Wallet>>::new, CategoryService::consumeTransfer, Map::putAll);
	}
	
	private static Map<Category, Set<Wallet>> consumeTransfer(
			Map<Category, Set<Wallet>> map, Transfer t) {
		
		Category category = t.getCategory();
		Set<Wallet> set = map.get(category);
		
		if (set == null) {
			set = new HashSet<Wallet>();
			map.put(category, set);
		}
		
		set.add(t.getWallet());
		
		return map;
	}
	
}
