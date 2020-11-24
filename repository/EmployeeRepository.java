package com.miniProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.miniProject.models.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
	Optional<Employee> findByName(String name);
	
	@Query(value="select email from MiniProject.employee where id in"
			+ "(select employee_id from MiniProject.employee_role where role_id =3)", nativeQuery=true)
	List<String> getUserEmailId();
	
	@Query(value="select id, name, email from MiniProject.employee where id in"
			+ "(select employee_id from MiniProject.employee_role where role_id =3)", nativeQuery=true)
	List<String> getAdmins();
	
	@Query(value="select id, name, email from MiniProject.employee where id in"
			+ "(select employee_id from MiniProject.employee_role where role_id =2)", nativeQuery=true)
	List<String> getSuperAdmins();

	Boolean existsByName(String name);

	Boolean existsByEmail(String email);

	Optional<Employee> findByEmail(String email);
}
