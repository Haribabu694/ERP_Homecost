package com.project.service;

import java.util.List;

import com.project.entity.AdjustOrder;

public interface AdjService {

	List<AdjustOrder> findAll();

	void save(AdjustOrder adjOrder);

	List<AdjustOrder> filterByItem(String item);

	AdjustOrder findById(String item);

}
