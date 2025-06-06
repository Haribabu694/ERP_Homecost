package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.entity.Login;
import com.project.service.LoginServiceimpl;

@Controller
public class UpdatePasswordController {

	@Autowired
	private LoginServiceimpl loginService;
	
	@GetMapping("/updatepassword")
	public String showUpdatePassword() {

		return "UpdatePassword";
	}

	@PostMapping("/updatepassword")
	public String updatePassword(@RequestParam String username,
	                             @RequestParam String newpassword,
	                             @RequestParam String confirmpassword,
	                             Login login,
	                             Model model) {

	    // Check if username is empty
	    if (username == null || username.trim().isEmpty()) {
	        model.addAttribute("error", "Enter Username");
	        return "UpdatePassword";
	    }

	    // Check if new password is empty
	    if (newpassword == null || newpassword.trim().isEmpty()) {
	        model.addAttribute("error", "Enter New Password");
	        return "UpdatePassword";
	    }

	    // Check if confirm password is empty
	    if (confirmpassword == null || confirmpassword.trim().isEmpty()) {
	        model.addAttribute("error", "Enter Confirm Password");
	        return "UpdatePassword";
	    }

	    // Check if new and confirm password match
	    if (!newpassword.equals(confirmpassword)) {
	        model.addAttribute("error", "New and Confirm Passwords do not match.");
	        return "UpdatePassword";
	    }

	    // Validate password strength
	    String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

	    if (!newpassword.matches(passwordPattern)) {
	        model.addAttribute("error", "Password must be at least 8 characters long and include one uppercase letter, one number, and one special character.");
	        return "UpdatePassword";
	    }

	    // Update password
	    boolean updated = loginService.updatePassword(username, newpassword);
	    if (updated) {
	        model.addAttribute("success", "Password updated successfully!");
	        loginService.sendResetPassword(login); // send password email only if update was successful
	    } else {
	        model.addAttribute("error", "Failed to update password. Please try again!");
	    }

	    return "UpdatePassword";
	}

}
