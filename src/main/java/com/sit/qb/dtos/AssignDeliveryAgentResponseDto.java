package com.sit.qb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignDeliveryAgentResponseDto {
	private String message;
	private Long orderId;
	private Long agentId;
	private String agentName;
}
