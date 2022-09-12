package com.example.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payroll.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	
}
