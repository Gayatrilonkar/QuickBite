package com.sit.qb.dtos;

import com.sit.qb.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryReportDto {
	private String agentName;
	private String customerName;
	private Long orderId;
	private OrderStatus status;
	private Double total;
}
