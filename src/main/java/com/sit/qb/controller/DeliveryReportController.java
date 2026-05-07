package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.service.DeliveryAgentServiceImpl;

@RestController
@RequestMapping("api/delivery")
public class DeliveryReportController {

	@Autowired
	private DeliveryAgentServiceImpl service;

	@GetMapping("/report")
	public List<DeliveryReportDto> getDeliveryReport(@RequestParam(required = false) String status) {
		return service.getDeliveryReport(status);
	}
}
