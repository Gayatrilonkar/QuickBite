package com.sit.qb.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.Order;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;

@Service
public class OrderServiceImpl {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	public Order placeOrder(OrderRequestDto orderDto) {
		Customer customer = customerServiceImpl.getCustomer(orderDto.getCustomer_id());
		if(customer != null) {
			List<Long> menuIds = orderDto.getItems().stream().map(menuQty -> menuQty.getMenuItemId()).collect(Collectors.toList());
		}
		return null;
	}	
	
}
