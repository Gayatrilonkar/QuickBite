package com.sit.qb.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.MenuItemPriceResponseDto;
import com.sit.qb.dtos.MenuItemSearchResponseDto;
import com.sit.qb.dtos.TopMenuItemDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.repository.MenuItemRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MenuItemServiceImpl {

	@Autowired
	private MenuItemRepository menuItemRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public List<TopMenuItemDto> getTop3OrderedMenuItems() {
		return menuItemRepository.findTopOrderedMenuItems(PageRequest.of(0, 3));
	}

	@Transactional(readOnly = true)
	public List<MenuItemPriceResponseDto> getAvailableMenuItemsBelowPrice(String maxPrice) {
		Double parsedMaxPrice = parseMaxPrice(maxPrice);

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MenuItem> criteriaQuery = criteriaBuilder.createQuery(MenuItem.class);
		Root<MenuItem> root = criteriaQuery.from(MenuItem.class);

		Predicate pricePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), parsedMaxPrice);
		Predicate availablePredicate = criteriaBuilder.equal(root.get("isAvailable"), true);

		criteriaQuery.select(root).where(criteriaBuilder.and(pricePredicate, availablePredicate));

		return entityManager.createQuery(criteriaQuery)
				.getResultList()
				.stream()
				.map(menuItem -> new MenuItemPriceResponseDto(
						menuItem.getId(),
						menuItem.getName(),
						menuItem.getPrice(),
						menuItem.getCategory()))
				.toList();
	}

	public List<MenuItemSearchResponseDto> searchMenuItems(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"keyword query parameter is required and cannot be blank");
		}

		return menuItemRepository.searchByKeyword(keyword.trim())
				.stream()
				.map(menuItem -> new MenuItemSearchResponseDto(
						menuItem.getId(),
						menuItem.getName(),
						menuItem.getPrice(),
						menuItem.getCategory(),
						menuItem.getIsAvailable()))
				.toList();
	}

	private Double parseMaxPrice(String maxPrice) {
		if (maxPrice == null || maxPrice.trim().isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"maxPrice query parameter is required");
		}

		try {
			BigDecimal price = new BigDecimal(maxPrice.trim());
			if (price.compareTo(BigDecimal.ZERO) <= 0 || price.scale() > 2) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"maxPrice must be positive and have at most 2 decimal places");
			}
			return price.doubleValue();
		} catch (NumberFormatException exception) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"maxPrice must be a valid numeric value");
		}
	}
}
