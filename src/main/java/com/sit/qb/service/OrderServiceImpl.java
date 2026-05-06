package com.sit.qb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.MenuQuantity;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.OrderItem;
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

		if (customer != null) {
			List<Long> menuIds = orderDto.getItems().stream().map(menuQty -> menuQty.getMenuItemId())
					.collect(Collectors.toList());

			List<Boolean> isMenuExists = new ArrayList<>();
			for (long id : menuIds) {
				MenuItem menuItem = menuItemRepository.findById(id).get();
				
				if(menuItem != null) if(menuItem.getIsAvailable() == true) isMenuExists.add(true);
				else isMenuExists.add(false);
			}
			
			if(!isMenuExists.contains(false)) {
				Order order = new Order();
				order.setCustomer(customer);
				order.setOrderDate(LocalDateTime.now());
				double totalAmount = 0.0;
				
				List<OrderItem> orderItems = new ArrayList<>();
				
				for(MenuQuantity itemDto : orderDto.getItems()) {
					MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId()).orElse(null);
					
					if(menuItem != null && menuItem.getIsAvailable()) {
						OrderItem orderItem = new OrderItem();
						orderItem.setMenuItem(menuItem);
						orderItem.setQuantity((int) itemDto.getQuantity());
						orderItem.setUnitPrice(menuItem.getPrice());
						orderItem.setOrder(order);
						
						orderItems.add(orderItem);
						
						totalAmount += menuItem.getPrice() * itemDto.getQuantity();
					}
				}
				
				order.setOrderItems(orderItems);
				order.setTotalAmount(totalAmount);
				
				return repository.save(order);
			} else {
			    System.out.println("Menu item unavailable");
			}
		}
		return null;
	}
	
}
