package com.sit.qb.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sit.qb.dtos.TopMenuItemDto;
import com.sit.qb.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long>{

	@Query("""
			SELECT m FROM MenuItem m
			WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	List<MenuItem> searchByKeyword(@Param("keyword") String keyword);

	@Query("""
			SELECT NEW com.sit.qb.dtos.TopMenuItemDto(m.name, COUNT(oi))
			FROM OrderItem oi
			JOIN oi.menuItem m
			GROUP BY m.id, m.name
			ORDER BY COUNT(oi) DESC
			""")
	List<TopMenuItemDto> findTopOrderedMenuItems(Pageable pageable);
}
