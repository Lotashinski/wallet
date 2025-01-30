package com.github.lotashinski.wallet.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.CategoryWallet;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Sum;
import com.github.lotashinski.wallet.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
	
	@Query("""
			SELECT w
			FROM Wallet w
			WHERE 
				id IN (:ids)
				AND w.creator = :creator
			""")
	Collection<Wallet> findByPersonAndIds(@Param("creator") Person person, @Param("ids") Collection<? extends UUID> ids);
	
	@Query("""
			SELECT cw
			FROM CategoryWallet cw
			INNER JOIN FETCH cw.wallet w
			INNER JOIN FETCH cw.category c
			WHERE c in (:categories)
			""")
	Collection<CategoryWallet> findByCategories(@Param("categories") Collection<? extends Category> categories);
	
	@Query("""
			SELECT new com.github.lotashinski.wallet.entity.Sum(t.currencyCode, sum(t.value)) 
			FROM Wallet w
			INNER JOIN w.transfers t
			WHERE w.creator = :person
			GROUP BY t.currencyCode
			ORDER BY t.currencyCode
			""")
	Collection<Sum> getSumForPerson(@Param("person") Person person);
	
	@Query("""
			SELECT w
			FROM Wallet w
			WHERE w.creator = :creator
				AND EXISTS(
					SELECT c2.id
					FROM Category c2
					INNER JOIN c2.categoryWallets cw2
					INNER JOIN cw2.wallet w2
					WHERE w2 = w
						AND c2 = :category 
				)
			ORDER BY lower(w.title)
			""")
	List<Wallet> findByPersonAndCategory(@Param("creator") Person person, @Param("category") Category category);
	
	@Query("""
			SELECT w
			FROM Wallet w
			WHERE 
				w.id = :id
				AND w.creator = :creator
			""")
	Optional<Wallet> findByPersonAndId(@Param("creator") Person person, @Param("id") UUID id);
	
	@Query("""
			SELECT w
			FROM Wallet w
			WHERE w.creator = :creator
			ORDER BY lower(w.title)
			""")
	List<Wallet> findByPerson(@Param("creator") Person person);
	
	
}
