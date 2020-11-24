package com.miniProject.mailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.miniProject.models.Employee;
import com.miniProject.repository.EmployeeRepository;


@Service
public class MailService {

	private JavaMailSender javaMailSender;
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	public MailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendEmail(Employee emp,String subject,String text) throws MailException {

		SimpleMailMessage mail = new SimpleMailMessage();
		
		mail.setTo(emp.getEmail());
		mail.setSubject(subject);
		mail.setText(text);
		
		javaMailSender.send(mail);
	}
}

