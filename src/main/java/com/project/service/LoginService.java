package com.project.service;

import com.project.entity.Login;

public interface LoginService {
	
	boolean findByUsernameandPassword(String username, String password);
	
	void createAccount(Login login);
	
	void sendForgotPassword(Login login);

}
