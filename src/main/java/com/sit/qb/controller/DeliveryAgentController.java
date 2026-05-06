package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.service.DeliveryAgentServiceImpl;

@RestController
@RequestMapping("api/agents")
public class DeliveryAgentController {

	@Autowired
	private DeliveryAgentServiceImpl service;
	
	@PostMapping()
	public DeliveryAgent addDeliveryAgent(@RequestBody DeliveryAgent agent) {
		return service.addDeliveryAgent(agent);
	}
}
