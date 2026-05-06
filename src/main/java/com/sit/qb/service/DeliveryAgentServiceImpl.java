package com.sit.qb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.repository.DeliveryAgentRepository;

@Service
public class DeliveryAgentServiceImpl {
	
	@Autowired
	private DeliveryAgentRepository repository;

	public DeliveryAgent addDeliveryAgent(DeliveryAgent agent) {
		return repository.save(agent);
	}

}
