package com.sit.qb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDetailsItemDto {
	private String item;
	private Integer qty;
	private Double price;
}
