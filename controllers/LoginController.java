package com.miniProject.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.miniProject.mailService.MailService;
import com.miniProject.models.Employee;
import com.miniProject.repository.EmployeeRepository;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/Login")
public class LoginController {

	@Autowired
	private MailService notificationService;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	PasswordEncoder encoder;

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@PutMapping("/forgotPassword/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String forgotPassword(@PathVariable String id) {
		
		Optional<Employee> employee=employeeRepository.findById(id);
		Employee emp=employee.get();
		
		String password = emp.generatePassword(8);
		String subject=" Password Reset";
		String text="New Password: "+password;
		emp.setPassword(encoder.encode(password));
		employeeRepository.save(emp);
		try {
			notificationService.sendEmail(emp,subject,text);
		} catch (MailException mailException) {
			System.out.println(mailException);
		}
		return "Password has been reset";
		
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('SUPERADMIN') or hasRole('ADMIN')")
	public String userAccess() {
		return "Viewing User Dashboard";
	}

	@GetMapping("/superAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	public String moderatorAccess() {
		return "Viewing Super admin content";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Viewing Admin content";
	}
}
