package com.github.lotashinski.wallet.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWallet {

	@Id
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@Id
	@ManyToOne
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	
		
	@Override
	public int hashCode() {
		return Objects.hash(category, wallet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryWallet other = (CategoryWallet) obj;
		return Objects.equals(category, other.category) && Objects.equals(wallet, other.wallet);
	}
	
}
