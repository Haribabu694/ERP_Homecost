package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@GetMapping("/login")
	public String showLoginForm() {
		
		return "Login";
	}
	
	@PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        if (loginService.findByUsernameandPassword(username, password)) {
            return "Home"; // successful login
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "Login"; // show login page again with error
        }
    }

}
