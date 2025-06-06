package com.project.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.entity.HomeCost;
import com.project.repository.HomeCostRepository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class HomeCostServiceimpl implements HomeCostService {

	private HomeCostRepository homeCostRepo;

	@Autowired // Optional in recent Spring versions
	public HomeCostServiceimpl(HomeCostRepository homeCostRepo) {
		this.homeCostRepo = homeCostRepo;
	}

	@Override
	public List<HomeCost> findAll() {

		return homeCostRepo.findAll();
	}

	@Override
	@Transactional
	public void save(HomeCost homecost) {
		homeCostRepo.save(homecost);
	}

	@Override
	public List<HomeCost> filterByItem(String item) {

		return homeCostRepo.findByItemContainingIgnoreCaseOrderByItemAsc(item);
	}

	@Override
	public HomeCost findById(Long sno) {

		return homeCostRepo.findById(sno).orElse(null);
	}
	
	@Override
	public Optional<HomeCost> findByID(Long id) {
	    return homeCostRepo.findById(id);
	}
	
	@Override
	public Long getNextSno() {
	    return homeCostRepo.findMaxSno() + 1;
	}

	@Override
	public void deleteById(Long sno) {
		 if (homeCostRepo.existsById(sno)) {
			 homeCostRepo.deleteById(sno);
	        } else {
	            throw new RuntimeException("Adjust Order with SNO " + sno + " not found.");
	        }		
	}
	
	@Override
	public Page<HomeCost> getPaginatedEntities(Pageable pageable) {
        return homeCostRepo.findAll(pageable);
    }
	
	@Override
	public Page<HomeCost> getFilteredEntities(String item, Integer year, Integer month, Pageable pageable) {
	    return homeCostRepo.findAll((root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        if (item != null && !item.isEmpty()) {
	            predicates.add(cb.like(cb.lower(root.get("item")), "%" + item.toLowerCase() + "%"));
	        }
	        if (year != null) {
	            Expression<Integer> yearExpr = cb.function("year", Integer.class, root.get("date"));
	            predicates.add(cb.equal(yearExpr, year));
	        }
	        if (month != null) {
	            Expression<Integer> monthExpr = cb.function("month", Integer.class, root.get("date"));
	            predicates.add(cb.equal(monthExpr, month));
	        }
	        return cb.and(predicates.toArray(new Predicate[0]));
	    }, pageable);
	}

	// For total sum calculation - non-paginated
	@Override
	public List<HomeCost> getFilteredEntities(String item, Integer year, Integer month) {
	    List<HomeCost> all = findAll(); // or fetch from repository directly
	    return all.stream().filter(order -> {
	        if (order.getDate() == null) return false;
	        LocalDate date = order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        boolean itemMatch = (item == null || item.isEmpty() || order.getItem().equalsIgnoreCase(item));
	        boolean yearMatch = (year == null || date.getYear() == year);
	        boolean monthMatch = (month == null || date.getMonthValue() == month);
	        return itemMatch && yearMatch && monthMatch;
	    }).collect(Collectors.toList());
	}
	
	@Override
	public Page<HomeCost> getAllEntities(Pageable pageable) {
	    return homeCostRepo.findAll(pageable);
	}

	@Override
	public List<HomeCost> getAllEntities() {
	    return homeCostRepo.findAll();
	}

	@Override
	public List<HomeCost> findAllByUsername(String username) {
	    return homeCostRepo.findByUsername(username);
	}
	
	@Override
	public List<HomeCost> getFilteredEntitiesByUser(String username, String item, Integer year, Integer month) {
	    Date[] dateRange = getDateRange(year, month);

	    if (item != null && !item.isEmpty() && dateRange != null) {
	        return homeCostRepo.findByUsernameAndItemContainingIgnoreCaseAndDateBetween(username, item, dateRange[0], dateRange[1]);
	    } else if (item != null && !item.isEmpty()) {
	        return homeCostRepo.findByUsernameAndItemContainingIgnoreCase(username, item);
	    } else if (dateRange != null) {
	        return homeCostRepo.findByUsernameAndDateBetween(username, dateRange[0], dateRange[1]);
	    } else {
	        return homeCostRepo.findByUsername(username);
	    }
	}

	@Override
	public Page<HomeCost> getFilteredEntitiesByUser(String username, String item, Integer year, Integer month, Pageable pageable) {
	    Date[] dateRange = getDateRange(year, month);

	    if (item != null && !item.isEmpty() && dateRange != null) {
	        return homeCostRepo.findByUsernameAndItemContainingIgnoreCaseAndDateBetween(username, item, dateRange[0], dateRange[1], pageable);
	    } else if (item != null && !item.isEmpty()) {
	        return homeCostRepo.findByUsernameAndItemContainingIgnoreCase(username, item, pageable);
	    } else if (dateRange != null) {
	        return homeCostRepo.findByUsernameAndDateBetween(username, dateRange[0], dateRange[1], pageable);
	    } else {
	        return homeCostRepo.findByUsername(username, pageable);
	    }
	}


	private Date[] getDateRange(Integer year, Integer month) {
	    if (year == null) return null;

	    LocalDate start;
	    LocalDate end;

	    if (month != null && month > 0) {
	        // Specific month
	        start = LocalDate.of(year, month, 1);
	        end = start.withDayOfMonth(start.lengthOfMonth());
	    } else {
	        // Whole year
	        start = LocalDate.of(year, 1, 1);
	        end = LocalDate.of(year, 12, 31);
	    }

	    return new Date[]{java.sql.Date.valueOf(start), java.sql.Date.valueOf(end)};
	}

}
