package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.entity.AdjustOrder;
import com.project.entity.Login;
import com.project.repository.AdjOrderRepository;

@Service
public class AdjServiceimpl implements AdjService {

	private AdjOrderRepository adjOrderRepo;

	@Autowired
	public AdjServiceimpl(AdjOrderRepository theAdjOrderRepo) {

		adjOrderRepo = theAdjOrderRepo;
	}

	@Override
	public void save(AdjustOrder adjOrder) {

		adjOrderRepo.save(adjOrder);
	}

	@Override
	public List<AdjustOrder> findAll() {

		return adjOrderRepo.findAll();
	}

	@Override
	public List<AdjustOrder> filterByItem(String item) {
	    return adjOrderRepo.findByItemContainingIgnoreCaseOrderByItemAsc(item);
	}

	@Override
	public AdjustOrder findById(String item) {

		return adjOrderRepo.findById(item).orElse(null); // Returns the AdjustOrder or null if not found

	}


}
