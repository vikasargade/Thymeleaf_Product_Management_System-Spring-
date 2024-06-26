package com.jsp.Thymeleaf_Project_Management_System.repositiory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.Thymeleaf_Project_Management_System.entity.Product;

public interface ProductRepositiory extends JpaRepository<Product, Integer> {

	public Product findByName(String name);
	
}
