package com.sit.qb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.AssignDeliveryAgentResponseDto;
import com.sit.qb.dtos.OrderDetailsResponseDto;
import com.sit.qb.dtos.OrderStatusFilterResponseDto;
import com.sit.qb.dtos.OrderTotalBillResponseDto;
import com.sit.qb.dtos.UpdateOrderStatusRequestDto;
import com.sit.qb.dtos.UpdateOrderStatusResponseDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.OrderItem;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.repository.DeliveryAgentRepository;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private DeliveryAgentRepository deliveryAgentRepository;

	@Mock
	private CustomerServiceImpl customerServiceImpl;

	@Mock
	private MenuItemRepository menuItemRepository;

	@Mock
	private EntityManager entityManager;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private CriteriaQuery<Order> criteriaQuery;

	@Mock
	private Root<Order> root;

	@Mock
	private Fetch<Object, Object> customerFetch;

	@Mock
	private Path<OrderStatus> statusPath;

	@Mock
	private Predicate predicate;

	@Mock
	private TypedQuery<Order> typedQuery;

	@InjectMocks
	private OrderServiceImpl service;

	@BeforeEach
	void setUp() throws Exception {
		var field = OrderServiceImpl.class.getDeclaredField("entityManager");
		field.setAccessible(true);
		field.set(service, entityManager);
	}

	@Test
	void updateOrderStatusAllowsPlacedToPreparingWithoutSave() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("PREPARING");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		UpdateOrderStatusResponseDto response = service.updateOrderStatus(10L, request);

		assertEquals(10L, response.getOrderId());
		assertEquals(OrderStatus.PLACED, response.getPreviousStatus());
		assertEquals(OrderStatus.PREPARING, response.getNewStatus());
		assertEquals(OrderStatus.PREPARING, order.getStatus());
		verify(orderRepository, never()).save(order);
	}

	@Test
	void updateOrderStatusAllowsPreparingToOutForDelivery() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PREPARING);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("OUT_FOR_DELIVERY");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		UpdateOrderStatusResponseDto response = service.updateOrderStatus(10L, request);

		assertEquals(OrderStatus.PREPARING, response.getPreviousStatus());
		assertEquals(OrderStatus.OUT_FOR_DELIVERY, response.getNewStatus());
		assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
	}

	@Test
	void updateOrderStatusAllowsOutForDeliveryToDelivered() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

		DeliveryAgent agent = new DeliveryAgent();
		agent.setIsAvailable(false);
		order.setDeliveryAgent(agent);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("DELIVERED");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		UpdateOrderStatusResponseDto response = service.updateOrderStatus(10L, request);

		assertEquals(OrderStatus.OUT_FOR_DELIVERY, response.getPreviousStatus());
		assertEquals(OrderStatus.DELIVERED, response.getNewStatus());
		assertEquals(OrderStatus.DELIVERED, order.getStatus());
		assertEquals(true, agent.getIsAvailable());
	}

	@Test
	void updateOrderStatusAllowsPlacedToCancelled() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("CANCELLED");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		UpdateOrderStatusResponseDto response = service.updateOrderStatus(10L, request);

		assertEquals(OrderStatus.PLACED, response.getPreviousStatus());
		assertEquals(OrderStatus.CANCELLED, response.getNewStatus());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());
	}

	@Test
	void updateOrderStatusReturnsNotFoundForMissingOrder() {
		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("PREPARING");

		when(orderRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.updateOrderStatus(99L, request));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void updateOrderStatusRejectsTerminalStatuses() {
		Order deliveredOrder = new Order();
		deliveredOrder.setId(10L);
		deliveredOrder.setStatus(OrderStatus.DELIVERED);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("CANCELLED");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(deliveredOrder));

		ResponseStatusException deliveredException = assertThrows(
				ResponseStatusException.class,
				() -> service.updateOrderStatus(10L, request));

		assertEquals(HttpStatus.BAD_REQUEST, deliveredException.getStatusCode());

		Order cancelledOrder = new Order();
		cancelledOrder.setId(11L);
		cancelledOrder.setStatus(OrderStatus.CANCELLED);
		when(orderRepository.findById(11L)).thenReturn(Optional.of(cancelledOrder));

		ResponseStatusException cancelledException = assertThrows(
				ResponseStatusException.class,
				() -> service.updateOrderStatus(11L, request));

		assertEquals(HttpStatus.BAD_REQUEST, cancelledException.getStatusCode());
	}

	@Test
	void updateOrderStatusRejectsInvalidStatusValue() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("COOKING");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.updateOrderStatus(10L, request));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals(OrderStatus.PLACED, order.getStatus());
	}

	@Test
	void updateOrderStatusRejectsIllegalTransition() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PREPARING);

		UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto();
		request.setStatus("CANCELLED");

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.updateOrderStatus(10L, request));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals(OrderStatus.PREPARING, order.getStatus());
	}

	@Test
	void getOrderTotalBillReturnsCalculatedAggregationTotal() {
		when(orderRepository.existsById(10L)).thenReturn(true);
		when(orderRepository.calculateTotalBillByOrderId(10L)).thenReturn(480.004);

		OrderTotalBillResponseDto response = service.getOrderTotalBill(10L);

		assertEquals(10L, response.getOrderId());
		assertEquals(480.00, response.getTotalBill());
		verify(orderRepository).calculateTotalBillByOrderId(10L);
	}

	@Test
	void getOrderTotalBillReturnsZeroWhenOrderHasNoItems() {
		when(orderRepository.existsById(10L)).thenReturn(true);
		when(orderRepository.calculateTotalBillByOrderId(10L)).thenReturn(null);

		OrderTotalBillResponseDto response = service.getOrderTotalBill(10L);

		assertEquals(10L, response.getOrderId());
		assertEquals(0.0, response.getTotalBill());
	}

	@Test
	void getOrderTotalBillReturnsNotFoundForMissingOrder() {
		when(orderRepository.existsById(99L)).thenReturn(false);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrderTotalBill(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		verify(orderRepository, never()).calculateTotalBillByOrderId(99L);
	}

	@Test
	void getOrderTotalBillReturnsBadRequestForNonPositiveOrderId() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrderTotalBill(0L));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(orderRepository, never()).existsById(0L);
	}

	@Test
	void getOrdersByStatusReturnsMatchingOrdersUsingCriteriaApi() {
		Customer rahul = new Customer();
		rahul.setName("Rahul Sharma");

		Order firstOrder = new Order();
		firstOrder.setId(10L);
		firstOrder.setStatus(OrderStatus.PLACED);
		firstOrder.setCustomer(rahul);
		firstOrder.setTotalAmount(480.00);

		Customer priya = new Customer();
		priya.setName("Priya Mehta");

		Order secondOrder = new Order();
		secondOrder.setId(12L);
		secondOrder.setStatus(OrderStatus.PLACED);
		secondOrder.setCustomer(priya);
		secondOrder.setTotalAmount(320.00);

		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Order.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(Order.class)).thenReturn(root);
		when(root.fetch("customer", jakarta.persistence.criteria.JoinType.LEFT)).thenReturn(customerFetch);
		when(root.<OrderStatus>get("status")).thenReturn(statusPath);
		when(criteriaBuilder.equal(statusPath, OrderStatus.PLACED)).thenReturn(predicate);
		when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
		when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(List.of(firstOrder, secondOrder));

		List<OrderStatusFilterResponseDto> response = service.getOrdersByStatus("PLACED");

		assertEquals(2, response.size());
		assertEquals(10L, response.get(0).getOrderId());
		assertEquals(OrderStatus.PLACED, response.get(0).getStatus());
		assertEquals("Rahul Sharma", response.get(0).getCustomer());
		assertEquals(480.00, response.get(0).getTotal());
		assertEquals(12L, response.get(1).getOrderId());
		assertEquals("Priya Mehta", response.get(1).getCustomer());
		assertEquals(320.00, response.get(1).getTotal());
		verify(criteriaBuilder).equal(statusPath, OrderStatus.PLACED);
	}

	@Test
	void getOrdersByStatusReturnsEmptyListWhenNoOrdersMatch() {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Order.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(Order.class)).thenReturn(root);
		when(root.fetch("customer", jakarta.persistence.criteria.JoinType.LEFT)).thenReturn(customerFetch);
		when(root.<OrderStatus>get("status")).thenReturn(statusPath);
		when(criteriaBuilder.equal(statusPath, OrderStatus.CANCELLED)).thenReturn(predicate);
		when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
		when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(List.of());

		List<OrderStatusFilterResponseDto> response = service.getOrdersByStatus("CANCELLED");

		assertEquals(0, response.size());
	}

	@Test
	void getOrdersByStatusReturnsBadRequestForInvalidStatus() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrdersByStatus("COOKING"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(entityManager, never()).getCriteriaBuilder();
	}

	@Test
	void getOrdersByStatusReturnsBadRequestForBlankStatus() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrdersByStatus("   "));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(entityManager, never()).getCriteriaBuilder();
	}

	@Test
	void getOrderDetailsReturnsNestedOrderDetails() {
		Customer customer = new Customer();
		customer.setName("Rahul Sharma");

		MenuItem paneerTikka = new MenuItem();
		paneerTikka.setName("Paneer Tikka");

		OrderItem firstItem = new OrderItem();
		firstItem.setMenuItem(paneerTikka);
		firstItem.setQuantity(2);
		firstItem.setUnitPrice(180.00);

		MenuItem biryani = new MenuItem();
		biryani.setName("Biryani");

		OrderItem secondItem = new OrderItem();
		secondItem.setMenuItem(biryani);
		secondItem.setQuantity(1);
		secondItem.setUnitPrice(120.00);

		Order order = new Order();
		order.setId(10L);
		order.setCustomer(customer);
		order.setStatus(OrderStatus.PLACED);
		order.setOrderDate(LocalDateTime.of(2025, 4, 19, 10, 30));
		order.setOrderItems(List.of(firstItem, secondItem));
		order.setTotalAmount(480.00);

		when(orderRepository.findOrderDetailsById(10L)).thenReturn(Optional.of(order));

		OrderDetailsResponseDto response = service.getOrderDetails(10L);

		assertEquals(10L, response.getOrderId());
		assertEquals("Rahul Sharma", response.getCustomerName());
		assertEquals(OrderStatus.PLACED, response.getStatus());
		assertEquals(LocalDateTime.of(2025, 4, 19, 10, 30), response.getOrderDate());
		assertEquals(480.00, response.getTotal());
		assertEquals(2, response.getItems().size());
		assertEquals("Paneer Tikka", response.getItems().get(0).getItem());
		assertEquals(2, response.getItems().get(0).getQty());
		assertEquals(180.00, response.getItems().get(0).getPrice());
		assertEquals("Biryani", response.getItems().get(1).getItem());
		assertEquals(1, response.getItems().get(1).getQty());
		assertEquals(120.00, response.getItems().get(1).getPrice());
	}

	@Test
	void getOrderDetailsReturnsNotFoundForMissingOrder() {
		when(orderRepository.findOrderDetailsById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrderDetails(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void getOrderDetailsReturnsBadRequestForNonPositiveOrderId() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getOrderDetails(0L));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(orderRepository, never()).findOrderDetailsById(0L);
	}

	@Test
	void assignDeliveryAgentUpdatesManagedEntitiesWithoutSave() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		DeliveryAgent agent = new DeliveryAgent();
		agent.setId(1L);
		agent.setName("Ravi Kumar");
		agent.setIsAvailable(true);

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
		when(deliveryAgentRepository.findById(1L)).thenReturn(Optional.of(agent));

		AssignDeliveryAgentResponseDto response = service.assignDeliveryAgent(10L, 1L);

		assertSame(agent, order.getDeliveryAgent());
		assertFalse(agent.getIsAvailable());
		assertEquals("Agent Ravi Kumar assigned to Order #10", response.getMessage());
		assertEquals(10L, response.getOrderId());
		assertEquals(1L, response.getAgentId());
		assertEquals("Ravi Kumar", response.getAgentName());
		verify(orderRepository, never()).save(order);
	}

	@Test
	void assignDeliveryAgentReturnsNotFoundForMissingOrder() {
		when(orderRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.assignDeliveryAgent(99L, 1L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void assignDeliveryAgentReturnsNotFoundForMissingAgent() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
		when(deliveryAgentRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.assignDeliveryAgent(10L, 99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void assignDeliveryAgentReturnsConflictForUnavailableAgent() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.PLACED);

		DeliveryAgent agent = new DeliveryAgent();
		agent.setId(1L);
		agent.setIsAvailable(false);

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
		when(deliveryAgentRepository.findById(1L)).thenReturn(Optional.of(agent));

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.assignDeliveryAgent(10L, 1L));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void assignDeliveryAgentReturnsBadRequestForTerminalOrder() {
		Order order = new Order();
		order.setId(10L);
		order.setStatus(OrderStatus.DELIVERED);

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.assignDeliveryAgent(10L, 1L));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}
}
