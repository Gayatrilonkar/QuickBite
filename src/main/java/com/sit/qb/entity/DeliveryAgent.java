package com.sit.qb.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String phone;
	private Boolean isAvailable = true;
	
	@OneToMany(mappedBy = "deliveryAgent", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Order> orders = new ArrayList<>();
}
