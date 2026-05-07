package com.sit.qb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.DeliveryAgentRequestDto;
import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.repository.DeliveryAgentRepository;
import com.sit.qb.repository.OrderRepository;

@Service
public class DeliveryAgentServiceImpl {
	
	@Autowired
	private DeliveryAgentRepository repository;

	@Autowired
	private OrderRepository orderRepository;

	public DeliveryAgent addDeliveryAgent(DeliveryAgentRequestDto request) {
		validateDeliveryAgentRequest(request);

		DeliveryAgent agent = new DeliveryAgent();
		agent.setName(request.getName().trim());
		agent.setPhone(request.getPhone().trim());
		agent.setIsAvailable(true);

		return repository.save(agent);
	}

	public List<DeliveryReportDto> getDeliveryReport(String status) {
		if (status == null || status.trim().isEmpty()) {
			return orderRepository.findDeliveryReport();
		}

		try {
			return orderRepository.findDeliveryReportByStatus(OrderStatus.valueOf(status.trim().toUpperCase()));
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Invalid status value: " + status);
		}
	}

	private void validateDeliveryAgentRequest(DeliveryAgentRequestDto request) {
		if (request == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Delivery agent request body is required");
		}

		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"name is required");
		}

		if (request.getName().trim().length() > 100) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"name must be at most 100 characters");
		}

		if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"phone is required");
		}

		if (request.getPhone().trim().length() > 15) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"phone must be at most 15 characters");
		}
	}
}
