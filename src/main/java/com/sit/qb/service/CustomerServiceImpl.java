package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.Customer;
import com.sit.qb.repository.CustomerRepository;

@Service
public class CustomerServiceImpl {

	@Autowired
	private CustomerRepository repository;
	
	public Customer register(Customer customer) {
		return repository.save(customer);
	}

	public Customer getCustomer(long id) {
		Optional<Customer> customer = repository.findById(id);
		if(customer.isPresent()) {
			return customer.get();
		}
		return null;
	}
	
	public Customer getCustomerByName(String name) {
		Optional<Customer> customer = repository.findByName(name);
		if(customer.isPresent()) {
			return customer.get();
		}
		return null;
	}

	public Customer getCustomerByEmailAndPhone(String email, String phone) {
		Optional<Customer> customer = repository.findByEmailAndPhone(email, phone);
		if(customer.isPresent()) {
			return customer.get();
		}
		return null;
	}

	public List<Customer> getAllCustomers() {
		return repository.findAll();
	}

	public void deleteCustomer(long id) {
		repository.deleteById(id);
	}


	
}
