package com.github.lotashinski.wallet.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.Formula;

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
	private UUID id;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "currency", nullable = true, length = 5)
	private String currency;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
	private List<Transfer> transfers = new ArrayList<>();
	
	@Setter(value = AccessLevel.NONE)
	@Formula("""
			(select sum(t.value)
			from transfer t
			where t.wallet_id = id)
			""")
	private BigDecimal value;
	
	@ManyToOne
	@JoinColumn(name = "creator_id", nullable = false)
	private Person creator;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
	private Set<CategoryWallet> categoryWallets = new HashSet<>();
	
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
	
	public BigDecimal getValue() {
		return value == null 
				? BigDecimal.ZERO 
				: value;
	}
	
	public void addCategory(Category category) {
		if (! getCategories().contains(category)) {
			return;
		}
		
		var cw = new CategoryWallet(category, this);
		addCategoryWallet(cw);
		category.addCategoryWallet(cw);
	}
	
	public void removeCategory(Category category) {
		if (! getCategories().contains(category)) {
			return;
		}
		
		var cwSet = getCategoryWallets(); 
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
