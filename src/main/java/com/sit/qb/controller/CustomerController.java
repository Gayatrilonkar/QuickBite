package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.Customer;
import com.sit.qb.service.CustomerServiceImpl;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
	
	@Autowired
	private CustomerServiceImpl service;
	
	@PostMapping
	public Customer register(@RequestBody Customer customer) {
		return service.register(customer);
	}
	
	@GetMapping("/{id}")
	public Customer getCustomer(@PathVariable long id) {
		return service.getCustomer(id);
	}
	
	@GetMapping("/byname/{name}")
	public Customer getCustomerByName(@PathVariable String name) {
		return service.getCustomerByName(name);
	}
	
	@GetMapping("/{email}/{phone}")
	public Customer getCustomerByEmailAndPhone(@PathVariable String email, @PathVariable String phone) {
		return service.getCustomerByEmailAndPhone(email, phone);
	}
	
	@GetMapping
	public List<Customer> getAllCustomers() {
		return service.getAllCustomers();
	}
	
	@DeleteMapping("/{id}")
	public String deleteCustomer(@PathVariable long id) {
		service.deleteCustomer(id);
		return "Deleted";
	}
	
}
