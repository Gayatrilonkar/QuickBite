package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.CustomerOrderResponseDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.repository.CustomerRepository;
import com.sit.qb.repository.CustomerSummary;
import com.sit.qb.repository.OrderRepository;

@Service
public class CustomerServiceImpl {

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private OrderRepository orderRepository;
	
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

	public List<CustomerSummary> getCustomerSummaries() {
		return repository.findAllCustomerSummaries();
	}

	public void deleteCustomer(long id) {
		repository.deleteById(id);
	}

	public List<CustomerOrderResponseDto> getCustomerOrders(Long customerId) {
		if (customerId == null || customerId <= 0) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"customerId must be a positive number");
		}

		if (!repository.existsById(customerId)) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Customer not found with id: " + customerId);
		}

		return orderRepository.findAllByCustomerIdOrderByOrderDateDesc(customerId)
				.stream()
				.map(order -> new CustomerOrderResponseDto(
						order.getId(),
						order.getStatus(),
						order.getTotalAmount(),
						order.getOrderDate()))
				.toList();
	}


	
}
