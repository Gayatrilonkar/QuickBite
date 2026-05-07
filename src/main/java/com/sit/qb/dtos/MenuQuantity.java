package com.sit.qb.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuQuantity {

	@Min(value = 1, message = "menuItemId must be positive")
	private long menuItemId;

	@Min(value = 1, message = "quantity must be positive")
	private long quantity;
}
