package com.bridgeIt.javaConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
@Component
public class Consumer {

	@Autowired
	JavaMailSender mailSender;
	
	
	void consumed(String message) {
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("tradefinancebridgelabz@gmail.com");
		mailMessage.setTo("shivrajpujari62@gmail.com");
		mailMessage.setText("Mail sent through rabbitMq "+message);
		mailSender.send(mailMessage);
		System.out.println("mail is sent");
		
	}
	
	
}
