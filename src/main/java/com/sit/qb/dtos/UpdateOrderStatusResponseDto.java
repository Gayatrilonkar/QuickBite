package com.sit.qb.dtos;

import java.time.LocalDateTime;

import com.sit.qb.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOrderStatusResponseDto {
	private Long orderId;
	private OrderStatus previousStatus;
	private OrderStatus newStatus;
	private LocalDateTime updatedAt;
}
