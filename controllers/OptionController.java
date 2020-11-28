package com.miniProject.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.miniProject.models.Employee;
import com.miniProject.models.Role;
import com.miniProject.repository.EmployeeRepository;
import com.miniProject.repository.RoleRepository;
import com.miniProject.service.EmployeeService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/option")
public class OptionController {
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	EmployeeService employeeService;

	@Autowired
	RoleRepository roleRepository;
	

	@Autowired
	PasswordEncoder encoder;


	@PostMapping("/addAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Employee addAdmin(@RequestBody Employee emp) {
		Set<Role> roles = new HashSet<>();
		Role adminRole = roleRepository.findByName("ROLE_ADMIN")
				.orElseThrow(() -> new RuntimeException("Error: Role not found."));
		roles.add(adminRole);
		emp.setRoles(roles);
		String password = emp.generatePassword(8);
		emp.setPassword(encoder.encode(password));
		employeeRepository.save(emp);
		return emp;

	}
	
	@PostMapping("/addSuperAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Employee addSuperAdmin(@RequestBody Employee emp) {
		Set<Role> roles = new HashSet<>();
		Role superAdminRole = roleRepository.findByName("ROLE_SUPERADMIN")
				.orElseThrow(() -> new RuntimeException("Error: Role not found."));
		roles.add(superAdminRole);
		emp.setRoles(roles);
		String password = emp.generatePassword(8);
		emp.setPassword(encoder.encode(password));
		employeeRepository.save(emp);
		return emp;

	}
	
	@GetMapping("/viewAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public List<Employee> viewAdmin()
	{
		return employeeRepository.getAdmins();
		
	}
	
	@GetMapping("/viewSuperAdmin")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public List<Employee> viewSuperAdmin()
	{
		return employeeRepository.getSuperAdmins();
		
	}
	
	@PutMapping("/updateAdmin/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Employee updateAdmin(@RequestBody Employee emp,@PathVariable String id) {
		try {
		Set<Role> role = new HashSet<>();
		Role adminRole = roleRepository.findByName("ROLE_ADMIN")
				.orElseThrow(() -> new RuntimeException("Error: Role not found."));
		role.add(adminRole);
		emp.setRoles(role);
		emp.setId(id);
		String password = emp.generatePassword(8);
		emp.setPassword(encoder.encode(password));
		employeeRepository.save(emp);
		return emp;	
		}catch(NoSuchElementException e) {
			 e.printStackTrace();
		 }
		return null;
	}	
	
	@PutMapping("/updateSuperAdmin/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Employee updateSuperAdmin(@RequestBody Employee emp,@PathVariable String id) {
		try {
		Set<Role> role = new HashSet<>();
		Role superAdminRole = roleRepository.findByName("ROLE_SUPERADMIN")
				.orElseThrow(() -> new RuntimeException("Error: Role not found."));
		role.add(superAdminRole);
		emp.setRoles(role);
		emp.setId(id);
		String password = emp.generatePassword(8);
		emp.setPassword(encoder.encode(password));
		employeeRepository.save(emp);
		return emp;	
		}catch(NoSuchElementException e) {
			 e.printStackTrace();
		 }
		return null;
	}
	
	@DeleteMapping("/deleteAdmin/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public String deleteAdmin(@PathVariable("id") String id) {
		 try {
			 employeeRepository.deleteById(id);
			 return "Admin with Employee Id "+id+" Deleted";
		 }catch(NoSuchElementException e) {
			 return "NOT_FOUND";
		 }
	}
	
	@DeleteMapping("/deleteSuperAdmin/{id}")
	@PreAuthorize("hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public String deleteSuperAdmin(@PathVariable("id") String id) {
		 try {
			 employeeRepository.deleteById(id);
			 return "Super Admin with Employee Id "+id+" Deleted";
		 }catch(NoSuchElementException e) {
			 return "NOT_FOUND";
		 }
	}
	
	@PutMapping("/changePassword")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')" )
	public String changePassword(@RequestParam("Password") String password, @RequestParam("Re-enter") String password1) {
	    Employee emp = employeeService.findUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
	    if(password.equals(password1)) {
	    	emp.setPassword(encoder.encode(password));
			employeeRepository.save(emp);
	    	return "Password updated successfully";
	    }
	    return null;
	    
	}

}
