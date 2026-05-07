package com.sit.qb.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sit.qb.dtos.UpdateOrderStatusRequestDto;
import com.sit.qb.dtos.UpdateOrderStatusResponseDto;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.service.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	@Mock
	private OrderServiceImpl service;

	@InjectMocks
	private OrderController controller;

	@Test
	void updateOrderStatusUsesQueryParamWhenRequestBodyIsMissing() {
		UpdateOrderStatusRequestDto expectedRequest = new UpdateOrderStatusRequestDto();
		expectedRequest.setStatus("PREPARING");

		UpdateOrderStatusResponseDto expectedResponse = new UpdateOrderStatusResponseDto(
				1L,
				OrderStatus.PLACED,
				OrderStatus.PREPARING,
				LocalDateTime.of(2026, 5, 7, 12, 45));

		when(service.updateOrderStatus(1L, expectedRequest)).thenReturn(expectedResponse);

		UpdateOrderStatusResponseDto response = controller.updateOrderStatus(1L, null, "PREPARING");

		assertEquals(expectedResponse, response);
	}
}
