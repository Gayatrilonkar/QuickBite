package com.sit.qb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.MenuItemPriceResponseDto;
import com.sit.qb.dtos.MenuItemSearchResponseDto;
import com.sit.qb.dtos.TopMenuItemDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.repository.MenuItemRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

	@Mock
	private MenuItemRepository menuItemRepository;

	@Mock
	private EntityManager entityManager;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private CriteriaQuery<MenuItem> criteriaQuery;

	@Mock
	private Root<MenuItem> root;

	@Mock
	private Path<Double> pricePath;

	@Mock
	private Path<Boolean> isAvailablePath;

	@Mock
	private Predicate pricePredicate;

	@Mock
	private Predicate availablePredicate;

	@Mock
	private Predicate combinedPredicate;

	@Mock
	private TypedQuery<MenuItem> typedQuery;

	@InjectMocks
	private MenuItemServiceImpl service;

	@BeforeEach
	void setUp() throws Exception {
		var field = MenuItemServiceImpl.class.getDeclaredField("entityManager");
		field.setAccessible(true);
		field.set(service, entityManager);
	}

	@Test
	void getTop3OrderedMenuItemsReturnsTopItemsFromRepository() {
		List<TopMenuItemDto> topItems = List.of(
				new TopMenuItemDto("Paneer Tikka", 45L),
				new TopMenuItemDto("Biryani", 38L),
				new TopMenuItemDto("Vada Pav", 30L));

		when(menuItemRepository.findTopOrderedMenuItems(PageRequest.of(0, 3))).thenReturn(topItems);

		List<TopMenuItemDto> response = service.getTop3OrderedMenuItems();

		assertEquals(3, response.size());
		assertEquals("Paneer Tikka", response.get(0).getItemName());
		assertEquals(45L, response.get(0).getOrderCount());
		assertEquals("Biryani", response.get(1).getItemName());
		assertEquals(38L, response.get(1).getOrderCount());
		assertEquals("Vada Pav", response.get(2).getItemName());
		assertEquals(30L, response.get(2).getOrderCount());
		verify(menuItemRepository).findTopOrderedMenuItems(PageRequest.of(0, 3));
	}

	@Test
	void getTop3OrderedMenuItemsReturnsEmptyListWhenNoOrdersExist() {
		when(menuItemRepository.findTopOrderedMenuItems(PageRequest.of(0, 3))).thenReturn(List.of());

		List<TopMenuItemDto> response = service.getTop3OrderedMenuItems();

		assertEquals(0, response.size());
	}

	@Test
	void getAvailableMenuItemsBelowPriceReturnsMatchingAvailableItems() {
		MenuItem samosa = new MenuItem();
		samosa.setId(3L);
		samosa.setName("Samosa");
		samosa.setPrice(40.00);
		samosa.setCategory("Snack");
		samosa.setIsAvailable(true);

		MenuItem lassi = new MenuItem();
		lassi.setId(7L);
		lassi.setName("Lassi");
		lassi.setPrice(80.00);
		lassi.setCategory("Drink");
		lassi.setIsAvailable(true);

		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(MenuItem.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(MenuItem.class)).thenReturn(root);
		when(root.<Double>get("price")).thenReturn(pricePath);
		when(root.<Boolean>get("isAvailable")).thenReturn(isAvailablePath);
		when(criteriaBuilder.lessThanOrEqualTo(pricePath, 100.00)).thenReturn(pricePredicate);
		when(criteriaBuilder.equal(isAvailablePath, true)).thenReturn(availablePredicate);
		when(criteriaBuilder.and(pricePredicate, availablePredicate)).thenReturn(combinedPredicate);
		when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
		when(criteriaQuery.where(combinedPredicate)).thenReturn(criteriaQuery);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(List.of(samosa, lassi));

		List<MenuItemPriceResponseDto> response = service.getAvailableMenuItemsBelowPrice("100.00");

		assertEquals(2, response.size());
		assertEquals(3L, response.get(0).getId());
		assertEquals("Samosa", response.get(0).getName());
		assertEquals(40.00, response.get(0).getPrice());
		assertEquals("Snack", response.get(0).getCategory());
		assertEquals(7L, response.get(1).getId());
		assertEquals("Lassi", response.get(1).getName());
		assertEquals(80.00, response.get(1).getPrice());
		assertEquals("Drink", response.get(1).getCategory());
		verify(criteriaBuilder).lessThanOrEqualTo(pricePath, 100.00);
		verify(criteriaBuilder).and(pricePredicate, availablePredicate);
	}

	@Test
	void getAvailableMenuItemsBelowPriceReturnsEmptyListWhenNoItemsMatch() {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(MenuItem.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(MenuItem.class)).thenReturn(root);
		when(root.<Double>get("price")).thenReturn(pricePath);
		when(root.<Boolean>get("isAvailable")).thenReturn(isAvailablePath);
		when(criteriaBuilder.lessThanOrEqualTo(pricePath, 10.00)).thenReturn(pricePredicate);
		when(criteriaBuilder.equal(isAvailablePath, true)).thenReturn(availablePredicate);
		when(criteriaBuilder.and(pricePredicate, availablePredicate)).thenReturn(combinedPredicate);
		when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
		when(criteriaQuery.where(combinedPredicate)).thenReturn(criteriaQuery);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(List.of());

		List<MenuItemPriceResponseDto> response = service.getAvailableMenuItemsBelowPrice("10");

		assertEquals(0, response.size());
	}

	@Test
	void getAvailableMenuItemsBelowPriceReturnsBadRequestForZeroOrNegativeValue() {
		ResponseStatusException zeroException = assertThrows(
				ResponseStatusException.class,
				() -> service.getAvailableMenuItemsBelowPrice("0"));
		ResponseStatusException negativeException = assertThrows(
				ResponseStatusException.class,
				() -> service.getAvailableMenuItemsBelowPrice("-1"));

		assertEquals(HttpStatus.BAD_REQUEST, zeroException.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, negativeException.getStatusCode());
		verify(entityManager, never()).getCriteriaBuilder();
	}

	@Test
	void getAvailableMenuItemsBelowPriceReturnsBadRequestForNonNumericValue() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getAvailableMenuItemsBelowPrice("abc"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(entityManager, never()).getCriteriaBuilder();
	}

	@Test
	void getAvailableMenuItemsBelowPriceReturnsBadRequestForMoreThanTwoDecimalPlaces() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.getAvailableMenuItemsBelowPrice("100.123"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(entityManager, never()).getCriteriaBuilder();
	}

	@Test
	void searchMenuItemsReturnsMatchingItems() {
		MenuItem paneerTikka = new MenuItem();
		paneerTikka.setId(2L);
		paneerTikka.setName("Paneer Tikka");
		paneerTikka.setPrice(180.00);
		paneerTikka.setCategory("Starter");
		paneerTikka.setIsAvailable(true);

		MenuItem paneerButterMasala = new MenuItem();
		paneerButterMasala.setId(8L);
		paneerButterMasala.setName("Paneer Butter Masala");
		paneerButterMasala.setPrice(210.00);
		paneerButterMasala.setCategory("Main Course");
		paneerButterMasala.setIsAvailable(true);

		when(menuItemRepository.searchByKeyword("paneer"))
				.thenReturn(List.of(paneerTikka, paneerButterMasala));

		List<MenuItemSearchResponseDto> response = service.searchMenuItems(" paneer ");

		assertEquals(2, response.size());
		assertEquals(2L, response.get(0).getId());
		assertEquals("Paneer Tikka", response.get(0).getName());
		assertEquals(180.00, response.get(0).getPrice());
		assertEquals("Starter", response.get(0).getCategory());
		assertEquals(true, response.get(0).getIsAvailable());
		assertEquals(8L, response.get(1).getId());
		assertEquals("Paneer Butter Masala", response.get(1).getName());
		assertEquals(210.00, response.get(1).getPrice());
		assertEquals("Main Course", response.get(1).getCategory());
		assertEquals(true, response.get(1).getIsAvailable());
		verify(menuItemRepository).searchByKeyword("paneer");
	}

	@Test
	void searchMenuItemsReturnsEmptyListWhenNoMatchesExist() {
		when(menuItemRepository.searchByKeyword("pizza")).thenReturn(List.of());

		List<MenuItemSearchResponseDto> response = service.searchMenuItems("pizza");

		assertEquals(0, response.size());
	}

	@Test
	void searchMenuItemsReturnsBadRequestForBlankKeyword() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> service.searchMenuItems("   "));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(menuItemRepository, never()).searchByKeyword("   ");
	}
}
