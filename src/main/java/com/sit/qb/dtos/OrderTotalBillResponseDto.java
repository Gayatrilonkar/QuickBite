package com.sit.qb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTotalBillResponseDto {
	private Long orderId;
	private Double totalBill;
}
