package com.project.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.HomeCost;
import com.project.entity.Login;
import com.project.repository.HomeCostRepository;
import com.project.repository.LoginRepository;
import com.project.service.HomeCostService;

import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeCostController {

	@Autowired
	private HomeCostService homecostserv;
	
	@Autowired
	private LoginRepository loginrepo;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private HomeCostRepository homeCostRepo;

	public HomeCostController(HomeCostService theHomecostserv) {

		homecostserv = theHomecostserv;
	}

	@GetMapping("/homeCostDashboard")
	public String HomeCostDashboard(@RequestParam(required = false) Integer year,
	                                @RequestParam(required = false) Integer month,
	                                @RequestParam(defaultValue = "0") int page,
	                                @RequestParam(defaultValue = "10") int size,HttpSession session,
	                                Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }

		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
	    
	    List<HomeCost> allOrders = homecostserv.findAllByUsername(username); // ✅ Only user data

	    // Filtered records
	    List<HomeCost> filtered = allOrders.stream().filter(order -> {
	        if (order.getDate() == null) return false;
	        LocalDate date = order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        boolean yearMatch = (year == null || date.getYear() == year);
	        boolean monthMatch = (month == null || date.getMonthValue() == month);
	        return yearMatch && monthMatch;
	    }).collect(Collectors.toList());

	    // Total number of filtered records
	    int totalRecords = filtered.size();

	    // Paginate filtered list
	    int fromIndex = page * size;
	    int toIndex = Math.min(fromIndex + size, totalRecords);
	    List<HomeCost> pageRecords = (fromIndex > toIndex) ? Collections.emptyList() : filtered.subList(fromIndex, toIndex);
	   
	    // Years and Months for dropdowns
	    int currentYear = LocalDate.now().getYear();
	    List<Integer> years = IntStream.rangeClosed(2020, currentYear).boxed().collect(Collectors.toList());
	    Map<Object, Object> months = IntStream.rangeClosed(1, 12).boxed()
	            .collect(Collectors.toMap(i -> i,
	                    i -> Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	                    (a, b) -> a, LinkedHashMap::new));

	    int totalPages = (int) Math.ceil((double) totalRecords / size);

	    // Set model attributes
	    model.addAttribute("adjustOrders", pageRecords);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("pageSize", size);
	    model.addAttribute("totalRecords", totalRecords);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("years", years);
	    model.addAttribute("months", months);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedMonth", month);
	    model.addAttribute("filtered", false);

	    return "HomeCost";
	}

	@GetMapping("/newHomeCostOrder")
	public String newHomeCostOrder(HttpSession session,Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
		
		HomeCost homeCost = new HomeCost();

		// Auto-generate unique sno (primary key)
		Long nextSno = homecostserv.getNextSno();
		homeCost.setSno(nextSno);

		model.addAttribute("adjustOrder", homeCost);
		model.addAttribute("action", "new");
		return "NewHomeCostOrder";
	}

	@PostMapping("/homeCostsave")
	public String saveHomeCostOrder(@Valid @ModelAttribute("adjustOrder") HomeCost theHomeCost,
	                                BindingResult bindingResult, RedirectAttributes redirectAttributes,
	                                HttpSession session, Model model) {

	    if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("adjustOrder", theHomeCost);
	        return "NewHomeCostOrder";
	    }

	    String username = (String) session.getAttribute("loggedInUser");
	    theHomeCost.setUsername(username);

	    try {
	        homecostserv.save(theHomeCost);

	        // ✅ Get user email from repository
	        Optional<Login> loginOptional = loginrepo.findByUsername(username);
	        if (loginOptional.isPresent()) {
	            String email = loginOptional.get().getMailid();

	            // ✅ Send email with HomeCost data
	            sendHomeCostConfirmationEmail(email, loginOptional.get().getUsername(), theHomeCost);
	        }

	        redirectAttributes.addFlashAttribute("successMessage", "HomeCost record saved successfully!");
	    } catch (RuntimeException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        return "redirect:/NewHomeCostOrder";
	    }

	    return "redirect:/NewHomeCostOrder";
	}
	
	private void sendHomeCostConfirmationEmail(String to,String username, HomeCost cost) {
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setTo(to);
	        helper.setSubject("ERP HomeCost Order Confirmation");

	        String body = "<p>Dear <strong>" + username + "</strong>,</p>"
	        	    + "<p>Your ERP HomeCost entry has been saved successfully. Below are the details:</p>"
	        	    + "<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%; text-align: center;'>"
	        	    + "<thead>"
	        	    + "<tr style='background-color: pink; font-weight: bold;'>"
	        	    + "<th>Item ID</th><th>Item Name</th><th>Item Cost</th><th>Date</th>"
	        	    + "</tr>"
	        	    + "</thead>"
	        	    + "<tbody>"
	        	    + "<tr>"
	        	    + "<td>" + cost.getSno() + "</td>"
	        	    + "<td>" + cost.getItem() + "</td>"
	        	    + "<td>₹" + cost.getCost() + "</td>"
	        	    + "<td>" + new java.text.SimpleDateFormat("dd-MMM-yyyy").format(cost.getDate()) + "</td>"
	        	    + "</tr>"
	        	    + "</tbody>"
	        	    + "</table>"
	        	    + "<p>This is a system-generated email. Please do not reply.</p>"
	        	    + "<p>Regards,<br><strong>ERP Admin</strong></p>";


	        helper.setText(body, true); // Send as HTML
	        javaMailSender.send(message);

	    } catch (MessagingException e) {
	        e.printStackTrace(); // Or log the error
	    }
	}

	@GetMapping("/NewHomeCostOrder")
	public String showForm(HttpSession session,Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
		
		model.addAttribute("adjustOrder", new HomeCost());
		return "NewHomeCostOrder";
	}

	@GetMapping("/itemFilter")
	public String itemFilter(
	        @RequestParam(value = "item", required = false) String item,
	        @RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "month", required = false) Integer month,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size, HttpSession session,
	        Model model) throws JsonProcessingException {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);

	    Pageable pageable = PageRequest.of(page, size, Sort.by("sno").ascending());

	    boolean isFilterApplied = (item != null && !item.trim().isEmpty()) || year != null || month != null;

	    Page<HomeCost> filteredPage = homecostserv.getFilteredEntitiesByUser(username, item, year, month, pageable);
	    List<HomeCost> filteredList;
	    List<HomeCost> fullFilteredList = homecostserv.getFilteredEntitiesByUser(username, item, year, month);

	    if (isFilterApplied) {
	        filteredPage = homecostserv.getFilteredEntitiesByUser(username, item, year, month, pageable);
	        filteredList = filteredPage.getContent();
	        fullFilteredList = homecostserv.getFilteredEntitiesByUser(username, item, year, month);
	    } else {
	        filteredPage = homecostserv.getFilteredEntitiesByUser(username, null, null, null, pageable);
	        filteredList = filteredPage.getContent();
	        fullFilteredList = homecostserv.getFilteredEntitiesByUser(username, null, null, null);
	    }

	    double totalCost = fullFilteredList.stream()
	        .filter(order -> order.getCost() != null)
	        .mapToDouble(order -> order.getCost().doubleValue())
	        .sum();

	    int currentYear = LocalDate.now().getYear();
	    List<Integer> years = IntStream.rangeClosed(2020, currentYear).boxed().collect(Collectors.toList());
	    Map<Object, Object> months = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toMap(
	            i -> i,
	            i -> Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
	            (a, b) -> a,
	            LinkedHashMap::new
	    ));

	    model.addAttribute("adjustOrders", filteredList);
	    model.addAttribute("totalCost", totalCost);
	    model.addAttribute("filtered", isFilterApplied);
	    model.addAttribute("years", years);
	    model.addAttribute("months", months);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedMonth", month);
	    model.addAttribute("item", item);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("pageSize", size);
	    model.addAttribute("totalRecords", filteredPage.getTotalElements());
	    model.addAttribute("totalPages", filteredPage.getTotalPages());
	    model.addAttribute("filteredData", new ObjectMapper().writeValueAsString(filteredList));

	    return "HomeCost";
	}




	@GetMapping("/editAdjustOrder")
	public String editAdjustOrder(@RequestParam("id") Long id, HttpSession session, Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
		
		HomeCost adjustOrder = homecostserv.findById(id);
		if (adjustOrder != null) {
			model.addAttribute("adjustOrder", adjustOrder);
			return "NewHomeCostOrder"; // Return to your edit page view
		} else {
			model.addAttribute("message", "HomeCost Order not found.");
			return "redirect:/HomeCost"; // Redirect back to dashboard
		}
	}

	@GetMapping("/viewAdjustOrder")
	public String viewAdjustOrder(@RequestParam("id") Long id,
			@RequestParam(value = "action", required = false) String action, HttpSession session,Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
		String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
		
		HomeCost adjustOrder = homecostserv.findById(id); // Fetch AdjustOrder using id
		if (adjustOrder != null) {
			model.addAttribute("adjustOrder", adjustOrder); // Add AdjustOrder to the model
			model.addAttribute("action", action); // Add action (view/edit) to the model
			return "NewHomeCostOrder"; // Return the view that handles both view and edit actions
		} else {
			model.addAttribute("message", "HomeCost Order not found.");
			return "redirect:/HomeCost"; // Redirect to dashboard if not found
		}
	}

	@GetMapping("/deleteHomeCostOrder")
	public String deleteHomeCostOrder(@RequestParam("id") Long id, HttpSession session) {
	    String username = (String) session.getAttribute("loggedInUser");
	    if (username == null) {
	        return "redirect:/login";
	    }

	    try {
	        Optional<HomeCost> optionalCost = homecostserv.findByID(id);
	        if (optionalCost.isPresent()) {
	            HomeCost cost = optionalCost.get();

	            // Fetch email from Login entity
	            Optional<Login> loginOpt = loginrepo.findByUsername(username);
	            if (loginOpt.isPresent()) {
	                String email = loginOpt.get().getMailid(); // Assuming 'mailid' is the email field

	                // Send deletion confirmation email
	                sendHomeCostDeletionEmail(email, username, cost);
	            }

	            // Delete the record
	            homecostserv.deleteById(id);
	        }

	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
	    }

	    return "redirect:/homeCostDashboard";
	}
	
	private void sendHomeCostDeletionEmail(String to, String username, HomeCost cost) {
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setTo(to);
	        helper.setSubject("ERP HomeCost Order Deletion");

	        String body = "<p>Dear <strong>" + username + "</strong>,</p>"
	                + "<p><strong>Your ERP HomeCost entry has been deleted</strong>. Below were the details:</p>"
	                + "<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%; text-align: center;'>"
	                + "<thead>"
	                + "<tr style='background-color: #f8d7da; font-weight: bold;'>"
	                + "<th>Item ID</th><th>Item Name</th><th>Item Cost</th><th>Date</th>"
	                + "</tr>"
	                + "</thead>"
	                + "<tbody>"
	                + "<tr>"
	                + "<td>" + cost.getSno() + "</td>"
	                + "<td>" + cost.getItem() + "</td>"
	                + "<td>₹" + cost.getCost() + "</td>"
	                + "<td>" + new java.text.SimpleDateFormat("dd-MMM-yyyy").format(cost.getDate()) + "</td>"
	                + "</tr>"
	                + "</tbody>"
	                + "</table>"
	                + "<p>This is a system-generated email. Please do not reply.</p>"
	                + "<p>Regards,<br><strong>ERP Admin</strong></p>";

	        helper.setText(body, true);
	        javaMailSender.send(message);

	    } catch (MessagingException e) {
	        e.printStackTrace();
	    }
	}

	
	@GetMapping("/entities")
	public ResponseEntity<Page<HomeCost>> getEntities(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "sno") String sortBy,
	        @RequestParam(defaultValue = "asc") String sortDir,
	        @RequestParam(required = false) String item,
	        @RequestParam(required = false) Integer year,
	        @RequestParam(required = false) Integer month
	) {
	    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
	    Pageable pageable = PageRequest.of(page, size, sort);
	    
	    Page<HomeCost> result = homecostserv.getFilteredEntities(item, year, month, pageable);
	    return ResponseEntity.ok(result);
	}

	
	@GetMapping("/entities/total")
	public ResponseEntity<Double> getTotalCost() {
	    Double total = homeCostRepo.sumAllCosts(); // You need a query for this
	    return ResponseEntity.ok(total);
	}

}
