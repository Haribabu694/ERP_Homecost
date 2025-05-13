package com.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.entity.Login;
import com.project.repository.LoginRepository;

@Service
public class LoginServiceimpl implements LoginService{
	
	@Autowired
	private LoginRepository loginrepo;

	@Override
	public boolean findByUsernameandPassword(String username, String password) {
		
		return loginrepo.findByUsername(username).map(Login -> Login.getPassword().equals(password)).orElse(false);
	}
	
	public boolean checkOldPassword(String username, String oldPassword) {
        return loginrepo.findByUsername(username)
                .map(user -> user.getPassword().equals(oldPassword))
                .orElse(false);
    }

    public boolean updatePassword(String username, String newPassword) {
        Optional<Login> optionalUser = loginrepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            Login user = optionalUser.get();
            user.setPassword(newPassword);
            loginrepo.save(user);
            return true;
        }
        return false;
    }

}
