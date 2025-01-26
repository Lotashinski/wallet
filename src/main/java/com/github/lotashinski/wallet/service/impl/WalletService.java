package com.github.lotashinski.wallet.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.lotashinski.wallet.dto.ItemWalletValuedDto;
import com.github.lotashinski.wallet.dto.SaveWalletDto;
import com.github.lotashinski.wallet.dto.SelectedCategoryDto;
import com.github.lotashinski.wallet.dto.SelectedWalletsDto;
import com.github.lotashinski.wallet.dto.WalletDto;
import com.github.lotashinski.wallet.entity.Category;
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
		
		log.info("Get wallets. User {}", person.getId());
		Collection<Wallet> wallets = walletRepository.findByPerson(person);
		
		log.debug("Load transfers for wallets", person.getId());
		Map<UUID, ? extends Collection<? extends Transfer>> transfers = transferRepository
				.getByWalletOrderByTimeDesc(wallets)
				.stream()
				.collect(Collectors.groupingBy(t -> t.getWallet().getId()));
		
		log.debug("Map wallets", person.getId());
		return wallets
				.stream()
				.map(w -> {
						Collection<? extends Transfer> ts = transfers.get(w.getId());
						SumCalculator calculator = new SumCalculator(ts);
						
						return walletMapper.toItemDto(w, calculator.calculate());
					}
				)
				.toList();
	}

	@Override
	public WalletDto get(UUID id) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get wallet {}. User {}", id, person.getId());
		Wallet wallet = walletRepository.findByPersonAndId(person, id)
				.orElseThrow(() -> generateNotFoundException(id));
		
		Collection<? extends Transfer> transfers = transferRepository
				.getByWalletOrderByTimeDesc(wallet);
		
		SumCalculator calculator = new SumCalculator(transfers);
		
		return walletMapper.toDto(wallet, calculator.calculate());
	}

	@Override
	public WalletDto create(SaveWalletDto dto) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Create wallet {}. User {}", dto, person.getId());
		Wallet wallet = walletMapper.toEntity(dto, person);
		Wallet entity = walletRepository.save(wallet);
		
		return walletMapper.toDto(entity, List.of());
	}

	@Override
	public WalletDto update(UUID id, SaveWalletDto dto) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Update wallet {}, set {}. User {}", id, dto, person.getId());
		
		return walletRepository
				.findByPersonAndId(person, id)
				.map(e -> walletMapper.updateEntity(dto, e))
				.map(walletRepository::save)
				.map(w -> {
					SumCalculator calculator = new SumCalculator(transferRepository
							.getByWalletOrderByTimeDesc(w));
					
					return walletMapper.toDto(w, calculator.calculate());
				})
				.orElseThrow(() -> WalletService.generateNotFoundException(id));
	}

	@Override
	public void delete(UUID id) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Delete wallet {}. User {}", id, person.getId());
		Wallet entity = walletRepository
				.findByPersonAndId(person, id)
				.orElseThrow(() -> generateNotFoundException(id));
		
		walletRepository.delete(entity);
	}

	@Override
	public List<SelectedWalletsDto> getCategoryWallets(UUID categoryid) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get category {} wallets. User {}", categoryid, person.getId());
		Category category = categoryRepository
				.findByPersonAndId(person, categoryid)
				.orElseThrow(() -> categoryNotFoundException(categoryid));
		
		Collection<? extends Wallet> allWallets = walletRepository.findByPerson(person);
		Collection<? extends Wallet> categoryWallets = new HashSet<>(walletRepository.findByPersonAndCategory(person, category));
		
		return allWallets
				.stream()
				.map(w -> walletMapper.toSelectedDto(w, categoryWallets.contains(w)))
				.toList();
	}

	@Override
	public void setCategoryWallets(UUID categoryId, Collection<UUID> walletsIds) {
		Person person = SecurityHolderAdapter.getCurrentUser();
	
		log.info("Set category {} wallets. User {}", categoryId, person.getId());
		Category category = categoryRepository.findByPersonAndId(person, categoryId)
				.orElseThrow(() -> categoryNotFoundException(categoryId));
		
		Collection<? extends Wallet> newState = walletRepository.findByPersonAndIds(person, walletsIds);
		Collection<? extends Wallet> oldState = category.getWallets();
	
	
		Set<Wallet> forPersists = new HashSet<>(newState);
		forPersists.removeAll(oldState);
		
		Set<Wallet> forDelete = new HashSet<>(oldState);
		forDelete.removeAll(newState);
		
		forDelete.stream()
			.forEach(category::removeWallet);
		
		forPersists.stream()
			.forEach(category::addWallet);
		
		categoryRepository.save(category);
	}
	

	@Override
	public List<SelectedCategoryDto> getWalletCategories(UUID walletId) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Get wallet {} categories. User {}", walletId, person.getId());
		Wallet entity = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> generateNotFoundException(walletId));
		
		Collection<? extends Category> allCategories = categoryRepository.findByPerson(person);
		Collection<? extends Category> walletCategories = new HashSet<>(categoryRepository.findByPersonAndWallet(person, entity));
		
		return allCategories
				.stream()
				.map(e -> categoryMapper.toDto(e, walletCategories.contains(e)))
				.toList();
	}

	@Override
	public void setWalletCategories(UUID walletId, Collection<UUID> categoriesIds) {
		Person person = SecurityHolderAdapter.getCurrentUser();
		
		log.info("Set wallet {} categories. User {}", walletId, person.getId());
		Wallet entity = walletRepository
				.findByPersonAndId(person, walletId)
				.orElseThrow(() -> generateNotFoundException(walletId));
		
		Collection<? extends Category> oldState = categoryRepository.findByPersonAndWallet(person, entity);
		Collection<? extends Category> newState = categoryRepository.findByPersonAndIds(person, categoriesIds);
		
		Set<Category> forPersists = new HashSet<>(newState);
		forPersists.removeAll(oldState);
		
		Set<Category> forDelete = new HashSet<>(oldState);
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
	

	private static RuntimeException categoryNotFoundException(UUID categoryId) {
		return new NotFoundHttpException(String.format("Category %s not found", categoryId));
	}

	
	private static RuntimeException generateNotFoundException(UUID id) {
		return new NotFoundHttpException(String.format("Wallet %s not found.", id.toString()));
	}
	
}
