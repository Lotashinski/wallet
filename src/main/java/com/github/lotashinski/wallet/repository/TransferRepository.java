package com.github.lotashinski.wallet.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
			LEFT JOIN t.category c
			WHERE t.id = :id AND w.creator = :creator
			ORDER BY t.time DESC 
			""")
	Optional<? extends Transfer> findByPersonAndId(@Param("creator") Person person, @Param("id") UUID id);
	
	@Query("""
			SELECT t
			FROM Transfer t
			INNER JOIN FETCH t.wallet w
			LEFT JOIN FETCH t.category c
			WHERE t.wallet = :wallet
			ORDER BY t.time DESC
			""")
	Collection<? extends Transfer> getByWalletOrderByTimeDesc(@Param("wallet") Wallet wallet);

	Page<? extends Transfer> getByWalletOrderByTimeDesc(Wallet wallet, Pageable pageable);
	
	@Query("""
			SELECT t
			FROM Transfer t
			INNER JOIN FETCH t.wallet w
			LEFT JOIN FETCH t.category c
			WHERE w IN (:wallets)
			ORDER BY t.time DESC
			""")
	List<? extends Transfer> getByWalletOrderByTimeDesc(@Param("wallets") Collection<Wallet> wallets);
	
	@Query("""
			SELECT t
			FROM Transfer t
			JOIN FETCH t.wallet w
			JOIN FETCH t.category c
			WHERE w.creator = :creator
			ORDER BY t.time DESC, t.value DESC 
			""")
	List<? extends Transfer> getForPersonOrderByTimeDesc(@Param("creator") Person person, Limit limit);
	
	@Query("""
			SELECT t
			FROM Transfer t
			JOIN FETCH t.wallet w
			JOIN FETCH t.category c
			WHERE c in (:categories)
			""")
	Collection<? extends Transfer> findByCategories(@Param("categories") Collection<? extends Category> categories);
	
	@Query("""
			SELECT t
			FROM Transfer t
			JOIN FETCH t.wallet w
			LEFT JOIN FETCH t.category c
			WHERE c in (:categories)
				AND t.time > :after
			ORDER BY t.time DESC
			""")
	List<? extends Transfer> findByCategoriesAfterTimestamp(
			@Param("categories") Collection<? extends Category> categories,
			@Param("after") LocalDateTime after);
	
	
	@Query("""
			SELECT t
			FROM Transfer t
			JOIN FETCH t.wallet w
			LEFT JOIN FETCH t.category c
			WHERE c in (:categories)
				AND t.time >= :start
				AND t.time <= :end
			ORDER BY t.time DESC
			""")
	List<? extends Transfer> findByCategoriesAndPersiod(
			@Param("categories") Collection<? extends Category> categories,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
}
