package com.example.payroll.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.payroll.entity.Employee;
import com.example.payroll.exceptions.EmployeeNotFoundException;
import com.example.payroll.repository.EmployeeRepository;

@RestController
public class EmployeeController {

	private final EmployeeRepository repository;
	
	public EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}
	
	//Listar funcionários com REST
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all(){
		List<EntityModel<Employee>> employees = repository.findAll().stream()
			      .map(employee -> EntityModel.of(employee,
			          linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
			          linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
			      .collect(Collectors.toList());
		
		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
		
	//Salvar novo funcionário
	@PostMapping("/employees")
	public Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}
	
	//Pesquisar um funcionário por id e jogar exceção caso não exista com REST
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {
		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		
		return EntityModel.of(employee,
			      linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
			      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
	
	//Alterar funcionário por id
	@PutMapping("/employees/{id}")
	public Employee replaceEmployee (@RequestBody Employee newEmployee, @PathVariable Long id) {
		return repository.findById(id)
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}).orElseGet(() -> {
					newEmployee.setId(id);
					return repository.save(newEmployee);
				});
	}
	
	//Deletar funcionário
	@DeleteMapping("/employees/{id}")
	public void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}
	
}
