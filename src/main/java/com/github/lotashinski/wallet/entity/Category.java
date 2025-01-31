package com.github.lotashinski.wallet.entity;

import java.util.ArrayList;
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
@Table(name="category")
@Getter 
@Setter
public class Category {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	@Setter(value = AccessLevel.PACKAGE)
	private UUID id;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@ManyToOne
	@JoinColumn(name = "creator_id", nullable = false)
	private Person creator;
	

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "category")
	private Set<CategoryWallet> categoryWallets = new HashSet<>();
	
	@OneToMany(mappedBy = "category")
	private List<Transfer> transfers = new ArrayList<>();
	
	
	public void addCategoryWallet(CategoryWallet categoryWallet) {
		var cwSet = getCategoryWallets();
		if (cwSet.contains(categoryWallet)) {
			return;
		}
		
		cwSet.add(categoryWallet);
	}
	
	public void removeCategoryWallet(CategoryWallet categoryWallet) {
		var cwSet = getCategoryWallets();
		if (! cwSet.contains(categoryWallet)) {
			return;
		}
		
		cwSet.remove(categoryWallet);
	}
	
	public void addWallet(Wallet wallet) {
		if (getWallets().contains(wallet)) {
			return;
		}
		
		var cw = new CategoryWallet(this, wallet);
		getCategoryWallets().add(cw);
	}
	
	public void removeWallet(Wallet wallet) {
		if (! getWallets().contains(wallet)) {
			return;
		}
		
		var cwSet = getCategoryWallets();
		cwSet.stream()
			.filter(cw -> cw.getWallet().equals(wallet))
			.findFirst()
			.ifPresent(cwSet::remove);
	}
	
	public Set<? extends Wallet> getWallets() {
		return getCategoryWallets().stream()
				.map(CategoryWallet::getWallet)
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
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}
	
}
