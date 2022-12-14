package com.example.payroll.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.payroll.entity.Employee;
import com.example.payroll.exceptions.EmployeeNotFoundException;
import com.example.payroll.features.EmployeeModelAssembler;
import com.example.payroll.repository.EmployeeRepository;

@RestController
public class EmployeeController {

	private final EmployeeRepository repository;
	
	private final EmployeeModelAssembler assembler;
	
	public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	//Listar funcionários com REST
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all(){
		List<EntityModel<Employee>> employees = repository.findAll().stream()
			      .map(assembler::toModel)
			      .collect(Collectors.toList());
		
		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
		
	//Salvar novo funcionário
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

	  EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

	  return ResponseEntity //
	      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
	      .body(entityModel);
	}
	
	//Pesquisar um funcionário por id e jogar exceção caso não exista com REST
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {
		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		
		return assembler.toModel(employee);
	}
	
	//Alterar funcionário por id
	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

	  Employee updatedEmployee = repository.findById(id) //
	      .map(employee -> {
	        employee.setName(newEmployee.getName());
	        employee.setRole(newEmployee.getRole());
	        return repository.save(employee);
	      }) //
	      .orElseGet(() -> {
	        newEmployee.setId(id);
	        return repository.save(newEmployee);
	      });

	  EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

	  return ResponseEntity //
	      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
	      .body(entityModel);
	}
	
	//Deletar funcionário
	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
	  repository.deleteById(id);
	  return ResponseEntity.noContent().build();
	}
	
}
