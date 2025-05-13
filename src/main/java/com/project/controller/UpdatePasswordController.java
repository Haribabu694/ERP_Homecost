package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String updatePassword(@RequestParam String username, @RequestParam String oldpassword,
			@RequestParam String newpassword, @RequestParam String confirmpassword, Model model) {

		// Step 1: Check old password
		if (!loginService.checkOldPassword(username, oldpassword)) {
			model.addAttribute("error", "Old password is incorrect.");
			return "UpdatePassword";
		}

		// Step 2: Check if new and confirm password match
		if (!newpassword.equals(confirmpassword)) {
			model.addAttribute("error", "New and confirm passwords does not match.");
			return "UpdatePassword";
		}

		// Step 3: Update password
		boolean updated = loginService.updatePassword(username, newpassword);
		if (updated) {
			model.addAttribute("success", "Password updated successfully.");
		} else {
			model.addAttribute("error", "Failed to update password. Please try again.");
		}

		return "UpdatePassword";
	}

}
