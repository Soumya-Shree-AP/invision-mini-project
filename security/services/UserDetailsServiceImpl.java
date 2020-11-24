package com.miniProject.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miniProject.models.Employee;
import com.miniProject.repository.EmployeeRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByName(name)
				.orElseThrow(() -> new UsernameNotFoundException("Employee Not Found with username: " + name));

		return UserDetailsImpl.build(employee);
	}

}
