package com.sit.qb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopMenuItemDto {
	private String itemName;
	private Long orderCount;
}
