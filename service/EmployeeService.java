package com.miniProject.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.miniProject.models.Employee;
import com.miniProject.repository.EmployeeRepository;

@Service("employeeService")
public class EmployeeService {
    @Autowired
    private EmployeeRepository repository;
    
    @Autowired
	PasswordEncoder encoder;

	public Employee findUserByName(String name) {
		Optional<Employee> emp=repository.findByName(name);
		return emp.get();
	}
	
	public Employee findUserByMail(String email) {
		Optional<Employee> emp=repository.findByEmail(email);
		return emp.get();
	}

}


