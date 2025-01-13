package com.github.lotashinski.wallet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.lotashinski.wallet.entity.Person;

public interface PersonRepository extends JpaRepository<Person, UUID> {

	Optional<Person> findOneByEmail(String email);
	
}
