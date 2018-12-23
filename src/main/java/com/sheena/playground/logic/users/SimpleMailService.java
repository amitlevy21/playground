package com.sheena.playground.logic.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SimpleMailService implements MailService{
	
	@Autowired
	private JavaMailSender emailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmailAddress;

	public SimpleMailService() {
	}

	@Override
	public void sendMessage(final Mail mail) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        message.setTo(mail.getTo());
        
        message.setFrom(fromEmailAddress);
        
        emailSender.send(message);
	}

}
