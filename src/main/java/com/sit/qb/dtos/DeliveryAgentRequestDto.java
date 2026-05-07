package com.sit.qb.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeliveryAgentRequestDto {
	@NotBlank(message = "name is required")
	@Size(max = 100, message = "name must be at most 100 characters")
	private String name;

	@NotBlank(message = "phone is required")
	@Size(max = 15, message = "phone must be at most 15 characters")
	private String phone;
}
