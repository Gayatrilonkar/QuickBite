package com.sit.qb.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
	@NotBlank(message = "status is required")
	private String status;
}
