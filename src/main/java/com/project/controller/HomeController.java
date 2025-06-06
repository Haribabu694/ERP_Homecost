package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@GetMapping("/home")
	public String showHome(HttpSession session, Model model) {
		
		if (session.getAttribute("loggedInUser") == null) {
	        return "redirect:/login";
	    }
		
	    String username = (String) session.getAttribute("loggedInUser");
	    model.addAttribute("username", username);
	    return "Home";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate(); // clear session
	    return "redirect:/login";
	}


}
