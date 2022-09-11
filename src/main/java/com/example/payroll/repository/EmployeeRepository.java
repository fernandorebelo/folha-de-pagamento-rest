package com.example.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payroll.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	/*
		Criando esta interface com o nome EmployeeRepository já ativa automaticamente:
    	- Create new Employees
    	- Update existing ones
    	- Delete Employees
    	- Find Employees (one, all, or search by simple or complex properties)
	 */
}
