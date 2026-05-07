package com.sit.qb.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

	@Min(value = 1, message = "customer_id must be positive")
	private long customer_id;

	@Valid
	@NotEmpty(message = "items must not be empty")
	private List<MenuQuantity> items;
	
	
	
}
