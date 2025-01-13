package com.github.lotashinski.wallet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.lotashinski.wallet.entity.Category;
import com.github.lotashinski.wallet.entity.Person;
import com.github.lotashinski.wallet.entity.Wallet;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
	
	@Query(value = """
			SELECT tc
			FROM Category tc
			WHERE :id = tc.id
				AND :creator = tc.creator
			ORDER BY tc.title
			""")
	Optional<Category> findByPersonAndId(@Param("creator")  Person person, @Param("id") UUID id);
	
	@Query(value = """
			SELECT tc
			FROM Category tc
			WHERE tc.creator = :creator
			ORDER BY tc.title
			""")
	List<Category> findByPerson(@Param("creator") Person person);
	
	@Query(value = """
			SELECT c
			FROM Category c
			WHERE c.creator = :creator
				AND EXISTS (
					SELECT w2
					FROM Wallet w2
					INNER JOIN w2.categoryWallets cw2
					INNER JOIN cw2.category c2
					WHERE w2 = :wallet AND c2 = c
				)
			ORDER BY lower(c.title)
			""")
	List<Category> findByPersonAndWallet(@Param("creator") Person person, @Param("wallet") Wallet wallet);
	
}
