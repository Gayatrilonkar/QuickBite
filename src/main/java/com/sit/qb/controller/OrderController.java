package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Order;
import com.sit.qb.service.OrderServiceImpl;

@RestController
@RequestMapping("api/orders")
public class OrderController {

	@Autowired
	private OrderServiceImpl service;
	
	@PostMapping
	public Order placeOrder(@RequestBody OrderRequestDto order) {
		
		return service.placeOrder(order) ;
	}
	
}
