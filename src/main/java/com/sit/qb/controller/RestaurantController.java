package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Restaurant;
import com.sit.qb.service.RestaurantServiceImpl;

@RestController
@RequestMapping("api/restaurants")
public class RestaurantController {
	
	@Autowired
	private RestaurantServiceImpl service;
	
	@PostMapping
	public Restaurant register(@RequestBody Restaurant restaurant) {
		return service.register(restaurant);
	}
	
	@GetMapping("/{id}")
	public Restaurant getRestaurant(@PathVariable long id) {
		return service.getRestaurant(id);
	}
	
	@GetMapping
	public List<Restaurant> getAllRestaurants() {
		return service.getAllRestaurants();
	}
	
	@DeleteMapping("/{id}")
	public String deleteRestaurant(@PathVariable long id) {
		service.deleteRestaurant(id);
		return "Deleted";
	}
	
	@PostMapping("/{id}/menu")
	public MenuItem addMenu(@RequestBody MenuItem menuItem, @PathVariable Long id) {
		return service.addMenu(menuItem, id);
	}
}
