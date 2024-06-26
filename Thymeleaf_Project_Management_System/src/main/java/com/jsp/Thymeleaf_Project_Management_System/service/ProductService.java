package com.jsp.Thymeleaf_Project_Management_System.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsp.Thymeleaf_Project_Management_System.entity.Product;
import com.jsp.Thymeleaf_Project_Management_System.repositiory.ProductRepositiory;

@Service
public class ProductService {

	@Autowired
	private ProductRepositiory productRepositiory;

	public List<Product> showProductList() {

		return productRepositiory.findAll();
	}

	public void saveProduct(Product product) {
		productRepositiory.save(product);
	}

	public Product getProductById(int id) {

		return productRepositiory.getById(id);
	}

	public Product saveUpdatedProduct(Product product) {
		
		return productRepositiory.save(product);

	}

	public void deleteProduct(Product product) {
		productRepositiory.delete(product);
	}

	public Product getSearchProduct(String search) {
		
		Product searchProduct =  productRepositiory.findByName(search);
		
		if(searchProduct!=null)
			return searchProduct;
		else
			return null;
	}

}
