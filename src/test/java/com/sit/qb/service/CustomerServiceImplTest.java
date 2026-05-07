package com.sit.qb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.CustomerOrderResponseDto;
import com.sit.qb.entity.Order;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.repository.CustomerRepository;
import com.sit.qb.repository.CustomerSummary;
import com.sit.qb.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private CustomerServiceImpl service;

	@Test
	void getCustomerSummariesReturnsProjectionList() {
		CustomerSummary rahul = new TestCustomerSummary(1L, "Rahul Sharma", "9876543210");
		CustomerSummary priya = new TestCustomerSummary(2L, "Priya Mehta", "9876541234");

		when(customerRepository.findAllCustomerSummaries()).thenReturn(List.of(rahul, priya));

		List<CustomerSummary> response = service.getCustomerSummaries();

		assertEquals(2, response.size());
		assertEquals(1L, response.get(0).getId());
		assertEquals("Rahul Sharma", response.get(0).getName());
		assertEquals("9876543210", response.get(0).getPhone());
		assertEquals(2L, response.get(1).getId());
		assertEquals("Priya Mehta", response.get(1).getName());
		assertEquals("9876541234", response.get(1).getPhone());
		verify(customerRepository).findAllCustomerSummaries();
		verify(customerRepository, never()).findAll();
	}

	@Test
	void getCustomerOrdersReturnsOrdersNewestFirstFromRepository() {
		Order newestOrder = new Order();
		newestOrder.setId(10L);
		newestOrder.setStatus(OrderStatus.PLACED);
		newestOrder.setTotalAmount(480.00);
		newestOrder.setOrderDate(LocalDateTime.of(2025, 4, 19, 10, 30));

		Order olderOrder = new Order();
		olderOrder.setId(7L);
		olderOrder.setStatus(OrderStatus.DELIVERED);
		olderOrder.setTotalAmount(250.00);
		olderOrder.setOrderDate(LocalDateTime.of(2025, 4, 18, 9, 15));

		when(customerRepository.existsById(1L)).thenReturn(true);
		when(orderRepository.findAllByCustomerIdOrderByOrderDateDesc(1L))
				.thenReturn(List.of(newestOrder, olderOrder));

		List<CustomerOrderResponseDto> response = service.getCustomerOrders(1L);

		assertEquals(2, response.size());
		assertEquals(10L, response.get(0).getOrderId());
		assertEquals(OrderStatus.PLACED, response.get(0).getStatus());
		assertEquals(480.00, response.get(0).getTotal());
		assertEquals(LocalDateTime.of(2025, 4, 19, 10, 30), response.get(0).getOrderDate());
		assertEquals(7L, response.get(1).getOrderId());
		assertEquals(OrderStatus.DELIVERED, response.get(1).getStatus());
		assertEquals(250.00, response.get(1).getTotal());
		assertEquals(LocalDateTime.of(2025, 4, 18, 9, 15), response.get(1).getOrderDate());
	}

	@Test
	void getCustomerOrdersReturnsEmptyListWhenCustomerHasNoOrders() {
		when(customerRepository.existsById(1L)).thenReturn(true);
		when(orderRepository.findAllByCustomerIdOrderByOrderDateDesc(1L)).thenReturn(List.of());

		List<CustomerOrderResponseDto> response = service.getCustomerOrders(1L);

		assertEquals(0, response.size());
	}

	@Test
	void getCustomerOrdersReturnsNotFoundForMissingCustomer() {
		when(customerRepository.existsById(99L)).thenReturn(false);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getCustomerOrders(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		verify(orderRepository, never()).findAllByCustomerIdOrderByOrderDateDesc(99L);
	}

	@Test
	void getCustomerOrdersReturnsBadRequestForNonPositiveCustomerId() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getCustomerOrders(0L));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(customerRepository, never()).existsById(0L);
	}

	private record TestCustomerSummary(Long id, String name, String phone) implements CustomerSummary {
		@Override
		public Long getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getPhone() {
			return phone;
		}
	}
}
