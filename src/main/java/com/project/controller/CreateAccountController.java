package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreateAccountController {
	
	@GetMapping("/createaccount")
	public String showCreateAccount() {
		
		return "CreateAccount";
	}

}
