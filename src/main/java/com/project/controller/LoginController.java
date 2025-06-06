package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.entity.Login;
import com.project.repository.LoginRepository;
import com.project.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginservice;

	@Autowired
	private LoginRepository loginRepo;

	@GetMapping("/login")
	public String showLoginpage() {

		return "Login";
	}

	@PostMapping("/login")
	public String Login(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {

		if (loginservice.findByUsernameandPassword(username, password)) {
			session.setAttribute("loggedInUser", username); // store username in session
			return "redirect:/home"; // successful login
		} else {
			model.addAttribute("error", "Invalid username or password");
			return "Login"; // show login page again with error
		}
	}
	
	@GetMapping("/createAccount")
	public String showCreateAccount() {
		
		return "createAccount";
	}

	@PostMapping("/createAccount")
	public String createAccount(@ModelAttribute Login login, Model model) {
		
		// Server-side check to prevent duplicate usernames
		if (loginRepo.existsByUsername(login.getUsername())) {
			model.addAttribute("errorMessage", "Username is already exist");
			return "createAccount"; // return same form with error message
		}
		
		// Server-side check to prevent duplicate Email
		if (loginRepo.existsBymailid(login.getMailid())) {
			model.addAttribute("errorMessage", "Email ID is already exist");
			return "createAccount"; // return same form with error message
		}

		loginservice.createAccount(login);
		model.addAttribute("successMessage", "Account created and password sent to your email");
		return "createAccount";
	}

	@GetMapping("/checkUsername")
	@ResponseBody
	public boolean checkUsername(@RequestParam String username) {
		return loginRepo.existsByUsername(username); // Returns true if exists
	}
	
	@GetMapping("/forgotpassword")
	public String showForgotPasswordForm(Model model) {
	    model.addAttribute("login", new Login()); // Optional, for form binding
	    return "Forgotpassword";
	}

	@PostMapping("/forgotpassword")
	public String forgotpassword(@ModelAttribute Login login, Model model) {
		
	    if (!loginRepo.existsBymailid(login.getMailid())) {
	        model.addAttribute("error", "Email does not exist");
	        return "Forgotpassword";
	    }

	    loginservice.sendForgotPassword(login);
	    
	    // Proceed to send reset email or show success message
	    model.addAttribute("success", "Password reset instructions sent to your email");
	    return "Forgotpassword";
	}


}
