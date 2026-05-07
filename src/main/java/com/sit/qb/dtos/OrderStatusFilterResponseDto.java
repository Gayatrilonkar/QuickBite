package com.sit.qb.dtos;

import com.sit.qb.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusFilterResponseDto {
	private Long orderId;
	private OrderStatus status;
	private String customer;
	private Double total;
}
