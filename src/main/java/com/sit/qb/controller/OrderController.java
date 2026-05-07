package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.AssignDeliveryAgentResponseDto;
import com.sit.qb.dtos.OrderDetailsResponseDto;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.dtos.OrderStatusFilterResponseDto;
import com.sit.qb.dtos.OrderTotalBillResponseDto;
import com.sit.qb.dtos.UpdateOrderStatusRequestDto;
import com.sit.qb.dtos.UpdateOrderStatusResponseDto;
import com.sit.qb.entity.Order;
import com.sit.qb.service.OrderServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/orders")
public class OrderController {

	@Autowired
	private OrderServiceImpl service;
	
	@PostMapping
	public Order placeOrder(@Valid @RequestBody OrderRequestDto order) {
		
		return service.placeOrder(order) ;
	}

	@GetMapping
	public List<OrderStatusFilterResponseDto> getOrdersByStatus(@RequestParam String status) {
		return service.getOrdersByStatus(status);
	}

	@GetMapping("/{orderId}")
	public OrderDetailsResponseDto getOrderDetails(@PathVariable Long orderId) {
		return service.getOrderDetails(orderId);
	}

	@GetMapping("/{orderId}/total")
	public OrderTotalBillResponseDto getOrderTotalBill(@PathVariable Long orderId) {
		return service.getOrderTotalBill(orderId);
	}

	@PatchMapping("/{orderId}/status")
	public UpdateOrderStatusResponseDto updateOrderStatus(
			@PathVariable Long orderId,
			@Valid @RequestBody(required = false) UpdateOrderStatusRequestDto request,
			@RequestParam(required = false) String status) {
		if (request == null && status != null) {
			request = new UpdateOrderStatusRequestDto();
			request.setStatus(status);
		}
		return service.updateOrderStatus(orderId, request);
	}
	
	@PutMapping("/{orderId}/assign/agent/{agentId}")
	public AssignDeliveryAgentResponseDto assignDeliveryAgent(
			@PathVariable Long orderId,
			@PathVariable Long agentId) {
		return service.assignDeliveryAgent(orderId, agentId);
	}
	
}
