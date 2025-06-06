package com.project.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.project.entity.Login;
import com.project.repository.LoginRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class LoginServiceimpl implements LoginService{
	
	@Autowired
	private LoginRepository loginrepo;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public boolean findByUsernameandPassword(String username, String password) {
		
		return loginrepo.findByUsername(username).map(Login -> Login.getPassword().equals(password)).orElse(false);
	}
	
	public boolean checkOldPassword(String username, String oldpassword) {
		return loginrepo.findByUsername(username)
				.map(user -> user.getPassword().equals(oldpassword)).orElse(false);
	}
	
	public boolean updatePassword(String username, String newpassword) {
		Optional<Login> optionalUser = loginrepo.findByUsername(username);
		if(optionalUser.isPresent()) {
			Login user = optionalUser.get();
			user.setPassword(newpassword);
			loginrepo.save(user);
			return true;
		}
		return false;
	}
	
	public void createAccount(Login login) {
        // Generate password
        String password = generateRandomPassword(8);
        login.setPassword(password);

        // Save to DB
        loginrepo.save(login);

        // Send email
        sendPasswordEmail(login.getMailid(), login.getUsername(), password);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#&";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendPasswordEmail(String to, String username, String password) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Account Created In ERP");

            String body = "<p>Dear <strong>" + username + "</strong>,</p>" +"<br>" +
                    "<p>Welcome to ERP!</p>" +
                    "<p>Your user account has been created in ERP.</p>" +
                    "<p><strong>Your Username is: </strong>" + username + "</p>" +
                    "<p><strong>Your default password is: </strong>" + password + "</p>" +
                    "<br>" +
                    "<p>This is a system-generated email. Please do not reply.</p>" +
                    "<p>Please contact the ERP administrator for any further support.</p>" +
                    "<p>Thanks,<br/>Regards,<br/><strong>ERP Admin</strong></p>";

            helper.setText(body, true); // true enables HTML

            javaMailSender.send(message);	

        } catch (MessagingException e) {
            e.printStackTrace(); // or handle logging
        }
    }
    
    public void sendForgotPassword(Login login) {
    	// Generate new password
        String newPassword = generateRandomPassword(8);

        // Find user by email
        Optional<Login> optionalUser = loginrepo.findBymailid(login.getMailid());

        if (optionalUser.isPresent()) {
            Login existingUser = optionalUser.get();
            
            // Update password in DB
            existingUser.setPassword(newPassword);
            loginrepo.save(existingUser);

            // Send email with new password
            sendForgotPasswordEmail(existingUser.getMailid(), existingUser.getUsername(), newPassword);
        }
    }
    
    public void sendResetPassword(Login login) {

        // Find user by email
        Optional<Login> optionalUser = loginrepo.findByUsername(login.getUsername());

        if (optionalUser.isPresent()) {
            Login existingUser = optionalUser.get();
            
            // Send email with new password
            sendResetPasswordEmail(existingUser.getMailid(), existingUser.getUsername());
        }
    }
    
    private void sendForgotPasswordEmail(String to, String username, String password) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("ERP Forgot Password");

            String body = "<p>Dear <strong>" + username + "</strong>,</p>" +
                    "<p>Your ERP user account password has been changed.</p>" +
                    "<p><strong>Your default password is: </strong>" + password + "</p>" +
                    "<br>" +
                    "<p>This is a system-generated email. Please do not reply.</p>" +
                    "<p>Please contact the ERP administrator for any further support.</p>" +
                    "<p>Thanks,<br/>Regards,<br/><strong>ERP Admin</strong></p>";

            helper.setText(body, true); // true enables HTML

            javaMailSender.send(message);	

        } catch (MessagingException e) {
            e.printStackTrace(); // or handle logging
        }
    }
    
    private void sendResetPasswordEmail(String to, String username) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("ERP Reset Password");

            String body = "<p>Dear <strong>" + username + "</strong>,</p>" +
                    
                    "<p><strong>Your ERP user accoount password has been reset. </strong></p>" +
                    "<br>" +
                    "<p>This is a system-generated email. Please do not reply.</p>" +
                    "<p>Please contact the ERP administrator for any further support.</p>" +
                    "<p>Thanks,<br/>Regards,<br/><strong>ERP Admin</strong></p>";

            helper.setText(body, true); // true enables HTML

            javaMailSender.send(message);	

        } catch (MessagingException e) {
            e.printStackTrace(); // or handle logging
        }
    }


}
