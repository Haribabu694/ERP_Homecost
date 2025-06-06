package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HouseMembersController {
	
	@GetMapping("/housemembers")
	public String showTransferOrder() {
		
		return "HouseMembers";
	}


}
