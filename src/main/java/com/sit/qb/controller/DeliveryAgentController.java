package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.DeliveryAgentRequestDto;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.service.DeliveryAgentServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/agents")
public class DeliveryAgentController {

	@Autowired
	private DeliveryAgentServiceImpl service;
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryAgent addDeliveryAgent(@Valid @RequestBody DeliveryAgentRequestDto agent) {
		return service.addDeliveryAgent(agent);
	}
}
