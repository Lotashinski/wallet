package com.github.lotashinski.wallet.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name =  "wallet_currency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletCurrency {
	
	@Id
	@ManyToOne
	@JoinColumn(name = "wallet_id", nullable = false)
	@Setter(value = AccessLevel.PACKAGE)
	private Wallet wallet;
	
	@Id
	@Column(name = "code", nullable = false, length = 5)
	@Setter(value = AccessLevel.PACKAGE)
	private String code;
	
	
	@Override
	public int hashCode() {
		return Objects.hash(code, wallet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WalletCurrency other = (WalletCurrency) obj;
		return Objects.equals(code, other.code) && Objects.equals(wallet, other.wallet);
	}
	
}
