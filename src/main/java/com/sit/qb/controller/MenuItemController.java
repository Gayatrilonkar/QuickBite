package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.MenuItemPriceResponseDto;
import com.sit.qb.dtos.MenuItemSearchResponseDto;
import com.sit.qb.dtos.TopMenuItemDto;
import com.sit.qb.service.MenuItemServiceImpl;

@RestController
@RequestMapping("api/menu")
public class MenuItemController {

	@Autowired
	private MenuItemServiceImpl service;

	@GetMapping("/top3")
	public List<TopMenuItemDto> getTop3OrderedMenuItems() {
		return service.getTop3OrderedMenuItems();
	}

	@GetMapping
	public List<MenuItemPriceResponseDto> getAvailableMenuItemsBelowPrice(@RequestParam String maxPrice) {
		return service.getAvailableMenuItemsBelowPrice(maxPrice);
	}

	@GetMapping("/search")
	public List<MenuItemSearchResponseDto> searchMenuItems(@RequestParam String keyword) {
		return service.searchMenuItems(keyword);
	}
}
