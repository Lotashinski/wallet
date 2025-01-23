package com.github.lotashinski.wallet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfer")
@Getter @Setter
public class Transfer {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	@Setter(value = AccessLevel.PACKAGE)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "wallet_id", nullable = false)
	private Wallet wallet;
	
	@Column(name = "value", nullable = false, precision = 10, scale = 2)
	private BigDecimal value;
	
	@Column(name = "currency_сode", nullable = true, length = 5)
	private String currencyCode;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "time")
	private LocalDateTime time;
	
}
