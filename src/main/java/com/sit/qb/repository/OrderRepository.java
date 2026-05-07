package com.sit.qb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.entity.Order;
import com.sit.qb.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("""
			select distinct o
			from Order o
			join fetch o.customer
			left join fetch o.orderItems oi
			left join fetch oi.menuItem
			where o.id = :orderId
			""")
	Optional<Order> findOrderDetailsById(@Param("orderId") Long orderId);

	@Query("""
			select o
			from Order o
			where o.customer.id = :customerId
			order by o.orderDate desc
			""")
	List<Order> findAllByCustomerIdOrderByOrderDateDesc(@Param("customerId") Long customerId);

	@Query("""
			select sum(oi.unitPrice * oi.quantity)
			from OrderItem oi
			where oi.order.id = :orderId
			""")
	Double calculateTotalBillByOrderId(@Param("orderId") Long orderId);

	@Query("""
			select new com.sit.qb.dtos.DeliveryReportDto(
				a.name,
				c.name,
				o.id,
				o.status,
				o.totalAmount
			)
			from Order o
			join o.customer c
			join o.deliveryAgent a
			""")
	List<DeliveryReportDto> findDeliveryReport();

	@Query("""
			select new com.sit.qb.dtos.DeliveryReportDto(
				a.name,
				c.name,
				o.id,
				o.status,
				o.totalAmount
			)
			from Order o
			join o.customer c
			join o.deliveryAgent a
			where o.status = :status
			""")
	List<DeliveryReportDto> findDeliveryReportByStatus(@Param("status") OrderStatus status);
}
