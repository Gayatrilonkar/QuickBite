package com.sit.qb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
	Optional<Customer> findByName(String name);
	
	Optional<Customer> findByEmailAndPhone(String email, String phone);

	@Query("""
			select c.id as id, c.name as name, c.phone as phone
			from Customer c
			""")
	List<CustomerSummary> findAllCustomerSummaries();
}
