package com.github.lotashinski.wallet.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Transfer;
import com.github.lotashinski.wallet.entity.Wallet;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

	@Query("""
			SELECT t
			FROM Transfer t
			INNER JOIN t.wallet w
			WHERE t.id = :id AND w.creator = :creator
			ORDER BY t.time DESC 
			""")
	Optional<Transfer> findByPersonAndId(@Param("creator") Person person, @Param("id") UUID id);
	
	List<Transfer> getByWalletOrderByTimeDesc(Wallet wallet);

	@Query("""
			SELECT t
			FROM Transfer t
			INNER JOIN t.wallet w
			WHERE w.creator = :creator
			ORDER BY t.time DESC, t.value DESC 
			""")
	List<Transfer> getByOrderByTimeDesc(@Param("creator") Person person, Limit limit);
	
	@Query("""
			SELECT t
			FROM Transfer t
			INNER JOIN t.category c
			WHERE c in (:categories)
			""")
	Collection<Transfer> findByCategories(
			@Param("categories") Collection<? extends Category> categories);
	
}
