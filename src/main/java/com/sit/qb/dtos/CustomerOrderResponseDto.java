package com.sit.qb.dtos;

import java.time.LocalDateTime;

import com.sit.qb.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerOrderResponseDto {
	private Long orderId;
	private OrderStatus status;
	private Double total;
	private LocalDateTime orderDate;
}
