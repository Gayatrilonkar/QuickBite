package com.sit.qb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.DeliveryAgentRequestDto;
import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.repository.DeliveryAgentRepository;
import com.sit.qb.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class DeliveryAgentServiceImplTest {

	@Mock
	private DeliveryAgentRepository deliveryAgentRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private DeliveryAgentServiceImpl service;

	@Test
	void addDeliveryAgentRegistersAgentAsAvailable() {
		DeliveryAgentRequestDto request = new DeliveryAgentRequestDto();
		request.setName(" Ravi Kumar ");
		request.setPhone(" 9123456780 ");

		DeliveryAgent savedAgent = new DeliveryAgent();
		savedAgent.setId(1L);
		savedAgent.setName("Ravi Kumar");
		savedAgent.setPhone("9123456780");
		savedAgent.setIsAvailable(true);

		when(deliveryAgentRepository.save(any(DeliveryAgent.class))).thenReturn(savedAgent);

		DeliveryAgent response = service.addDeliveryAgent(request);

		assertEquals(1L, response.getId());
		assertEquals("Ravi Kumar", response.getName());
		assertEquals("9123456780", response.getPhone());
		assertTrue(response.getIsAvailable());
	}

	@Test
	void addDeliveryAgentReturnsBadRequestForMissingName() {
		DeliveryAgentRequestDto request = new DeliveryAgentRequestDto();
		request.setPhone("9123456780");

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.addDeliveryAgent(request));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(deliveryAgentRepository, never()).save(any(DeliveryAgent.class));
	}

	@Test
	void addDeliveryAgentReturnsBadRequestForMissingPhone() {
		DeliveryAgentRequestDto request = new DeliveryAgentRequestDto();
		request.setName("Ravi Kumar");

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.addDeliveryAgent(request));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(deliveryAgentRepository, never()).save(any(DeliveryAgent.class));
	}

	@Test
	void addDeliveryAgentReturnsBadRequestForTooLongFields() {
		DeliveryAgentRequestDto longNameRequest = new DeliveryAgentRequestDto();
		longNameRequest.setName("a".repeat(101));
		longNameRequest.setPhone("9123456780");

		ResponseStatusException longNameException = assertThrows(
				ResponseStatusException.class,
				() -> service.addDeliveryAgent(longNameRequest));

		DeliveryAgentRequestDto longPhoneRequest = new DeliveryAgentRequestDto();
		longPhoneRequest.setName("Ravi Kumar");
		longPhoneRequest.setPhone("1".repeat(16));

		ResponseStatusException longPhoneException = assertThrows(
				ResponseStatusException.class,
				() -> service.addDeliveryAgent(longPhoneRequest));

		assertEquals(HttpStatus.BAD_REQUEST, longNameException.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, longPhoneException.getStatusCode());
		verify(deliveryAgentRepository, never()).save(any(DeliveryAgent.class));
	}

	@Test
	void getDeliveryReportReturnsProjectedReportEntries() {
		List<DeliveryReportDto> report = List.of(
				new DeliveryReportDto("Ravi Kumar", "Rahul Sharma", 10L, OrderStatus.DELIVERED, 480.00),
				new DeliveryReportDto("Suresh Nair", "Priya Mehta", 12L, OrderStatus.OUT_FOR_DELIVERY, 320.00));

		when(orderRepository.findDeliveryReport()).thenReturn(report);

		List<DeliveryReportDto> response = service.getDeliveryReport(null);

		assertEquals(2, response.size());
		assertEquals("Ravi Kumar", response.get(0).getAgentName());
		assertEquals("Rahul Sharma", response.get(0).getCustomerName());
		assertEquals(10L, response.get(0).getOrderId());
		assertEquals(OrderStatus.DELIVERED, response.get(0).getStatus());
		assertEquals(480.00, response.get(0).getTotal());
		assertEquals("Suresh Nair", response.get(1).getAgentName());
		assertEquals("Priya Mehta", response.get(1).getCustomerName());
		assertEquals(12L, response.get(1).getOrderId());
		assertEquals(OrderStatus.OUT_FOR_DELIVERY, response.get(1).getStatus());
		assertEquals(320.00, response.get(1).getTotal());
	}

	@Test
	void getDeliveryReportReturnsEmptyListWhenNoOrdersHaveAgents() {
		when(orderRepository.findDeliveryReport()).thenReturn(List.of());

		List<DeliveryReportDto> response = service.getDeliveryReport("");

		assertEquals(0, response.size());
	}

	@Test
	void getDeliveryReportFiltersByStatusWhenProvided() {
		List<DeliveryReportDto> report = List.of(
				new DeliveryReportDto("Ravi Kumar", "Rahul Sharma", 10L, OrderStatus.DELIVERED, 480.00));

		when(orderRepository.findDeliveryReportByStatus(OrderStatus.DELIVERED)).thenReturn(report);

		List<DeliveryReportDto> response = service.getDeliveryReport("delivered");

		assertEquals(1, response.size());
		assertEquals(OrderStatus.DELIVERED, response.get(0).getStatus());
		verify(orderRepository, never()).findDeliveryReport();
	}

	@Test
	void getDeliveryReportReturnsBadRequestForInvalidStatus() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getDeliveryReport("COOKING"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(orderRepository, never()).findDeliveryReportByStatus(OrderStatus.DELIVERED);
	}
}
