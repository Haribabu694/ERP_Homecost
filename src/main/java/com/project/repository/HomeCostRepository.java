
package com.project.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.entity.HomeCost;

@Repository
public interface HomeCostRepository extends JpaRepository<HomeCost, Long> {

	List<HomeCost> findByItemContainingIgnoreCaseOrderByItemAsc(String item);

	Optional<HomeCost> findById(Long id); // Add this method to fetch by ID

	@Query("SELECT COALESCE(MAX(h.sno), 0) FROM HomeCost h")
	Long findMaxSno();

	Page<HomeCost> findAll(Pageable pageable);

	Page<HomeCost> findAll(Specification<HomeCost> spec, Pageable pageable);

	@Query("SELECT SUM(h.cost) FROM HomeCost h")
	Double sumAllCosts();

	List<HomeCost> findByUsername(String username);

	Page<HomeCost> findByUsername(String username, Pageable pageable);

	List<HomeCost> findByUsernameAndItemContainingIgnoreCaseAndDateBetween(String username, String item,
			LocalDate start, LocalDate end);

	List<HomeCost> findByUsernameAndItemContainingIgnoreCase(String username, String item);

	Page<HomeCost> findByUsernameAndItemContainingIgnoreCase(String username, String item, Pageable pageable);

	Page<HomeCost> findByUsernameAndItemContainingIgnoreCaseAndDateBetween(String username, String item, Date startDate,
			Date endDate, Pageable pageable);

	List<HomeCost> findByUsernameAndItemContainingIgnoreCaseAndDateBetween(String username, String item, Date startDate,
			Date endDate);
	
	List<HomeCost> findByUsernameAndDateBetween(String username, Date startDate, Date endDate);
	Page<HomeCost> findByUsernameAndDateBetween(String username, Date startDate, Date endDate, Pageable pageable);


}