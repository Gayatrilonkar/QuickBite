package com.sit.qb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
	Optional<Customer> findByName(String name);
	
	Optional<Customer> findByEmailAndPhone(String email, String phone);
}
