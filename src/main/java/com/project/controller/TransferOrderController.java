package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransferOrderController {
	
	@GetMapping("/transferorder")
	public String showTransferOrder() {
		
		return "TransferOrder";
	}

}
