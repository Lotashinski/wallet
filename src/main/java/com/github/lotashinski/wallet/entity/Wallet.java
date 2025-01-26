package com.github.lotashinski.wallet.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallet")
@Getter 
@Setter
public class Wallet {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	@Setter(value = AccessLevel.PACKAGE)
	private UUID id;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
	private Set<WalletCurrency> currencies = new HashSet<WalletCurrency>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
	private List<Transfer> transfers = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "creator_id", nullable = false)
	private Person creator;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
	private Set<CategoryWallet> categoryWallets = new HashSet<>();
	
	
	public void setCurrencyCodes(Collection<? extends String> codes) {
		Set<String> currentState = new HashSet<>(getCurrencyCodes());
		
		Set<String> forPersists = new HashSet<>(codes);
		forPersists.removeAll(currentState);
		
		Set<String> forRemove = new HashSet<>(currentState);
		forRemove.removeAll(codes);
		
		getCurrencies().removeIf(wc -> forRemove.contains(wc.getCode()));
		
		getCurrencies().addAll(forPersists
				.stream()
				.map(c -> new WalletCurrency(this, c))
				.toList()
				);
	}
	
	public List<? extends String> getCurrencyCodes() {
		return getCurrencies()
				.stream()
				.map(WalletCurrency::getCode)
				.sorted()
				.toList();
	}
	
	public void addCategoryWallet(CategoryWallet categoryWallet) {
		Set<CategoryWallet> cwSet = getCategoryWallets();
		if (cwSet.contains(categoryWallet)) {
			return;
		}
		
		cwSet.add(categoryWallet);
	}
	
	public void removeCategoryWallet(CategoryWallet categoryWallet) {
		Set<CategoryWallet> cwSet = getCategoryWallets();
		if (! cwSet.contains(categoryWallet)) {
			return;
		}
		
		cwSet.remove(categoryWallet);
	}
	
	public void addCategory(Category category) {
		if (getCategories().contains(category)) {
			return;
		}
		
		CategoryWallet cw = new CategoryWallet(category, this);
		addCategoryWallet(cw);
	}
	
	public void removeCategory(Category category) {
		if (! getCategories().contains(category)) {
			return;
		}
		
		Set<CategoryWallet> cwSet = getCategoryWallets(); 
		cwSet.stream()
			.filter(wc -> wc.getCategory().equals(category))
			.peek(category::removeCategoryWallet)
			.findFirst()
			.ifPresent(cwSet::remove);
	}

	public Set<? extends Category> getCategories() {
		return categoryWallets.stream()
				.map(CategoryWallet::getCategory)
				.collect(Collectors.toSet());
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wallet other = (Wallet) obj;
		return Objects.equals(id, other.id);
	}
	
}
