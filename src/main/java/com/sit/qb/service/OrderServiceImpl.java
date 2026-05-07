package com.sit.qb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.AssignDeliveryAgentResponseDto;
import com.sit.qb.dtos.MenuQuantity;
import com.sit.qb.dtos.OrderDetailsItemDto;
import com.sit.qb.dtos.OrderDetailsResponseDto;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.dtos.OrderStatusFilterResponseDto;
import com.sit.qb.dtos.OrderTotalBillResponseDto;
import com.sit.qb.dtos.UpdateOrderStatusRequestDto;
import com.sit.qb.dtos.UpdateOrderStatusResponseDto;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.OrderItem;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.repository.DeliveryAgentRepository;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class OrderServiceImpl {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	
	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private DeliveryAgentRepository deliveryAgentRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	public Order placeOrder(OrderRequestDto orderDto) {

	   
	    Customer customer = customerServiceImpl
	            .getCustomer(orderDto.getCustomer_id());

	    if (customer == null) {
	        throw new RuntimeException("Customer not found");
	    }

	  
	    Order order = new Order();
	    order.setCustomer(customer);
	    order.setOrderDate(LocalDateTime.now());

	    List<OrderItem> orderItems = new ArrayList<>();

	    double totalAmount = 0.0;

	   
	    for (MenuQuantity itemDto : orderDto.getItems()) {

	
	        MenuItem menuItem = menuItemRepository
	                .findById(itemDto.getMenuItemId())
	                .orElseThrow(() ->
	                        new RuntimeException(
	                                "Menu Item not found with id: "
	                                        + itemDto.getMenuItemId()
	                        )
	                );

	   
	        if (!menuItem.getIsAvailable()) {
	            throw new RuntimeException(
	                    menuItem.getName() + " is currently unavailable"
	            );
	        }

	        
	        OrderItem orderItem = new OrderItem();

	        orderItem.setMenuItem(menuItem);
	        orderItem.setQuantity((int) itemDto.getQuantity());
	        orderItem.setUnitPrice(menuItem.getPrice());
	        orderItem.setOrder(order);

	      
	        orderItems.add(orderItem);

	       
	        totalAmount += menuItem.getPrice() * itemDto.getQuantity();
	    }

	
	    order.setOrderItems(orderItems);
	    order.setTotalAmount(totalAmount);

	 
	    return repository.save(order);
	}

	@Transactional
	public UpdateOrderStatusResponseDto updateOrderStatus(
			Long orderId,
			UpdateOrderStatusRequestDto request) {
		Order order = repository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Order not found with id: " + orderId));

		OrderStatus previousStatus = order.getStatus();
		if (previousStatus == OrderStatus.DELIVERED || previousStatus == OrderStatus.CANCELLED) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Cannot update order from terminal status: " + previousStatus);
		}

		OrderStatus newStatus = parseOrderStatus(request);
		if (!isValidStatusTransition(previousStatus, newStatus)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Illegal order status transition from " + previousStatus + " to " + newStatus);
		}

		order.setStatus(newStatus);
		if (newStatus == OrderStatus.DELIVERED && order.getDeliveryAgent() != null) {
			order.getDeliveryAgent().setIsAvailable(true);
		}

		return new UpdateOrderStatusResponseDto(
				order.getId(),
				previousStatus,
				newStatus,
				LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public OrderTotalBillResponseDto getOrderTotalBill(Long orderId) {
		if (orderId == null || orderId <= 0) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"orderId must be a positive number");
		}

		if (!repository.existsById(orderId)) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Order not found with id: " + orderId);
		}

		Double totalBill = repository.calculateTotalBillByOrderId(orderId);
		if (totalBill == null) {
			totalBill = 0.0;
		}

		return new OrderTotalBillResponseDto(orderId, Math.round(totalBill * 100.0) / 100.0);
	}

	@Transactional(readOnly = true)
	public List<OrderStatusFilterResponseDto> getOrdersByStatus(String status) {
		if (status == null || status.trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"status query parameter is required");
		}

		OrderStatus statusEnum;
		try {
			statusEnum = OrderStatus.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Invalid status value: " + status);
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		root.fetch("customer", JoinType.LEFT);

		Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), statusEnum);
		criteriaQuery.select(root).where(statusPredicate);

		return entityManager.createQuery(criteriaQuery)
				.getResultList()
				.stream()
				.map(order -> new OrderStatusFilterResponseDto(
						order.getId(),
						order.getStatus(),
						order.getCustomer() != null ? order.getCustomer().getName() : null,
						order.getTotalAmount()))
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderDetailsResponseDto getOrderDetails(Long orderId) {
		if (orderId == null || orderId <= 0) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"orderId must be a positive number");
		}

		Order order = repository.findOrderDetailsById(orderId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Order not found with id: " + orderId));

		List<OrderDetailsItemDto> items = order.getOrderItems()
				.stream()
				.map(item -> new OrderDetailsItemDto(
						item.getMenuItem().getName(),
						item.getQuantity(),
						item.getUnitPrice()))
				.toList();

		return new OrderDetailsResponseDto(
				order.getId(),
				order.getCustomer().getName(),
				order.getStatus(),
				order.getOrderDate(),
				items,
				order.getTotalAmount());
	}

	@Transactional
	public AssignDeliveryAgentResponseDto assignDeliveryAgent(Long orderId, Long agentId) {
		Order order = repository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Order not found with id: " + orderId));

		if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Cannot assign delivery agent to a completed order");
		}

		DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Delivery agent not found with id: " + agentId));

		if (!Boolean.TRUE.equals(agent.getIsAvailable())) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Delivery agent is already assigned to another active order");
		}

		order.setDeliveryAgent(agent);
		agent.setIsAvailable(false);

		return new AssignDeliveryAgentResponseDto(
				"Agent " + agent.getName() + " assigned to Order #" + order.getId(),
				order.getId(),
				agent.getId(),
				agent.getName());
	}

	private OrderStatus parseOrderStatus(UpdateOrderStatusRequestDto request) {
		if (request == null || request.getStatus() == null || request.getStatus().trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"status is required");
		}

		try {
			return OrderStatus.valueOf(request.getStatus().trim().toUpperCase());
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Invalid status value: " + request.getStatus());
		}
	}

	private boolean isValidStatusTransition(OrderStatus previousStatus, OrderStatus newStatus) {
		return (previousStatus == OrderStatus.PLACED && newStatus == OrderStatus.PREPARING)
				|| (previousStatus == OrderStatus.PREPARING && newStatus == OrderStatus.OUT_FOR_DELIVERY)
				|| (previousStatus == OrderStatus.OUT_FOR_DELIVERY && newStatus == OrderStatus.DELIVERED)
				|| (previousStatus == OrderStatus.PLACED && newStatus == OrderStatus.CANCELLED);
	}
	
}
