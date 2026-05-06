package com.sit.qb.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sit.qb.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.PLACED;
	private Double totalAmount;

	@Column(updatable = false)
	private LocalDateTime orderDate = LocalDateTime.now();

	// Many orders → one customer
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	// Many orders → one agent
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_agent_id")
	private DeliveryAgent deliveryAgent;

	// One order → many items
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();
}


