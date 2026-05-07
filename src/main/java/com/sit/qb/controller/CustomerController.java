package com.sit.qb.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sit.qb.dtos.ApiSuccessResponseDto;
import com.sit.qb.dtos.CustomerOrderResponseDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.repository.CustomerSummary;
import com.sit.qb.service.CustomerServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerServiceImpl service;

	// Register Customer
	@PostMapping
	public ResponseEntity<ApiSuccessResponseDto<Customer>> register(
			@RequestBody @Valid Customer customer,
			HttpServletRequest request) {

		Customer savedCustomer = service.register(customer);

		ApiSuccessResponseDto<Customer> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.CREATED.value(),
						"Customer registered successfully",
						savedCustomer,
						request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// Get Customer Summaries
	@GetMapping("/summary")
	public ResponseEntity<ApiSuccessResponseDto<List<CustomerSummary>>> getCustomerSummaries(
			HttpServletRequest request) {

		List<CustomerSummary> summaries = service.getCustomerSummaries();

		ApiSuccessResponseDto<List<CustomerSummary>> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer summaries fetched successfully",
						summaries,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Get Customer By ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiSuccessResponseDto<Customer>> getCustomer(
			@PathVariable long id,
			HttpServletRequest request) {

		Customer customer = service.getCustomer(id);

		ApiSuccessResponseDto<Customer> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer fetched successfully",
						customer,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Get Customer Orders
	@GetMapping("/{customerId}/orders")
	public ResponseEntity<ApiSuccessResponseDto<List<CustomerOrderResponseDto>>> getCustomerOrders(
			@PathVariable Long customerId,
			HttpServletRequest request) {

		List<CustomerOrderResponseDto> orders =
				service.getCustomerOrders(customerId);

		ApiSuccessResponseDto<List<CustomerOrderResponseDto>> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer orders fetched successfully",
						orders,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Get Customer By Name
	@GetMapping("/byname/{name}")
	public ResponseEntity<ApiSuccessResponseDto<Customer>> getCustomerByName(
			@PathVariable String name,
			HttpServletRequest request) {

		Customer customer = service.getCustomerByName(name);

		ApiSuccessResponseDto<Customer> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer fetched successfully",
						customer,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Get Customer By Email And Phone
	@GetMapping("/{email}/{phone}")
	public ResponseEntity<ApiSuccessResponseDto<Customer>> getCustomerByEmailAndPhone(
			@PathVariable String email,
			@PathVariable String phone,
			HttpServletRequest request) {

		Customer customer =
				service.getCustomerByEmailAndPhone(email, phone);

		ApiSuccessResponseDto<Customer> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer fetched successfully",
						customer,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Get All Customers
	@GetMapping
	public ResponseEntity<ApiSuccessResponseDto<List<Customer>>> getAllCustomers(
			HttpServletRequest request) {

		List<Customer> customers = service.getAllCustomers();

		ApiSuccessResponseDto<List<Customer>> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"All customers fetched successfully",
						customers,
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}

	// Delete Customer
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessResponseDto<String>> deleteCustomer(
			@PathVariable long id,
			HttpServletRequest request) {

		service.deleteCustomer(id);

		ApiSuccessResponseDto<String> response =
				new ApiSuccessResponseDto<>(
						LocalDateTime.now(),
						HttpStatus.OK.value(),
						"Customer deleted successfully",
						"Deleted",
						request.getRequestURI());

		return ResponseEntity.ok(response);
	}
}