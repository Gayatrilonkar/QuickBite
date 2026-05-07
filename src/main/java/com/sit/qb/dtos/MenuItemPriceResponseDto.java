package com.sit.qb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuItemPriceResponseDto {
	private Long id;
	private String name;
	private Double price;
	private String category;
}
