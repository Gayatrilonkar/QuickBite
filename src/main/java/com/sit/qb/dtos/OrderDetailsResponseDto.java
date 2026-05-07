package com.sit.qb.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.sit.qb.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDetailsResponseDto {
	private Long orderId;
	private String customerName;
	private OrderStatus status;
	private LocalDateTime orderDate;
	private List<OrderDetailsItemDto> items;
	private Double total;
}
