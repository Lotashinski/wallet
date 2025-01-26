package com.github.lotashinski.wallet.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lotashinski.wallet.dto.ItemCategoryDto;
import com.github.lotashinski.wallet.dto.SaveCategoryDto;
import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.CategoryWallet;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Sum;
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
				.map(c ->  transferCategoryMapper.toDto(c, calculateLast30Days(c)))
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
				.map(c -> transferCategoryMapper.toDto(c, calculateLast30Days(c)))
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
	public List<? extends ItemCategoryDto> getAll() {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get categories. User {}", person.getId());
		Collection<? extends Category> categories = categoryRepository
				.findByPerson(person);
		Map<Category, ? extends Collection<? extends Sum>> calculatedSum = calculateLast30Days(categories);
		Map<Category, ? extends Collection<Wallet>> linkWalletMap = findByLinkedCategories(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, calculatedSum.get(e), linkWalletMap.get(e)))
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
		Map<Category, ? extends Collection<? extends Sum>> calculatedSum = calculateLast30Days(categories);
		
		return categories
				.stream()
				.map(e -> transferCategoryMapper.toDto(e, calculatedSum.get(e)))
				.toList();
	}
	
	private Collection<? extends Sum> calculateLast30Days(Category category) {
		return calculateLast30Days(List.of(category))
				.get(category);
	}
	
	private Map<Category, ? extends Collection<? extends Sum>> calculateLast30Days(Collection<? extends Category> categories) {
		Map<Category, Collection<? extends Sum>> calculated = new HashMap<>();
		
		getLast30DaysTransfers(categories)
			.entrySet()
			.stream()
			.forEach(es -> {
				Category category = es.getKey();
				Collection<? extends Transfer> transfers = es.getValue();
				
				SumCalculator calculator = new SumCalculator(transfers);
				calculated.put(category, calculator.calculate());
			});
		
		return calculated;
	}
	
	
	private Map<Category, ? extends Collection<? extends Transfer>> getLast30DaysTransfers(
			Collection<? extends Category> categories) {
		LocalDateTime current = LocalDateTime.now();
		LocalDateTime last30Days = current.minusDays(30);
		return transferRepository.findByCategoriesAfterTimestamp(categories, last30Days)
					.stream()
					.collect(Collectors.groupingBy(Transfer::getCategory));
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
	
}
