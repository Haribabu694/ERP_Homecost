package com.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.entity.HomeCost;

public interface HomeCostService {

	List<HomeCost> findAll();

	void save(HomeCost homecost);

	List<HomeCost> filterByItem(String item);

	HomeCost findById(Long sno);

	Long getNextSno();

	void deleteById(Long sno);
	
	Page<HomeCost> getPaginatedEntities(Pageable pageable);
	
	Page<HomeCost> getFilteredEntities(String item, Integer year, Integer month, Pageable pageable);
	
	List<HomeCost> getFilteredEntities(String item, Integer year, Integer month);
	
	Page<HomeCost> getAllEntities(Pageable pageable);
	
	List<HomeCost> getAllEntities();

	List<HomeCost> findAllByUsername(String username);
	
	List<HomeCost> getFilteredEntitiesByUser(String username, String item, Integer year, Integer month);
	
	Page<HomeCost> getFilteredEntitiesByUser(String username, String item, Integer year, Integer month, Pageable pageable);
	
	Optional<HomeCost> findByID(Long id);
}
