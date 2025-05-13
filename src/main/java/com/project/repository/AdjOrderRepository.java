package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.AdjustOrder;

public interface AdjOrderRepository extends JpaRepository<AdjustOrder, String>{
	
	List<AdjustOrder> findByItemContainingIgnoreCaseOrderByItemAsc(String item);
	 
	 Optional<AdjustOrder> findById(String id); // Add this method to fetch by ID

}
