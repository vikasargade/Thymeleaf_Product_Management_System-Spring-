package com.jsp.Thymeleaf_Project_Management_System.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.Thymeleaf_Project_Management_System.dto.ProductDto;
import com.jsp.Thymeleaf_Project_Management_System.entity.Product;
import com.jsp.Thymeleaf_Project_Management_System.service.ProductService;

import jakarta.persistence.criteria.Path;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/product")
public class ProductsController {

	@Autowired
	private ProductService productService;

	@RequestMapping("/indexPage")
	public String showAllProducts(Model model) {

		List<Product> products = productService.showProductList();
		model.addAttribute("products", products);
		return "index";
	}

	@RequestMapping("/addProduct")
	public String addProduct(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "AddProduct";

	}

	@RequestMapping("/createProduct")
	public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) // valid is
																											// for to
																											// validate
																											// data for
																											// productDto
																											// bindingresult
																											// id for
																											// validation
																											// error
	{
		if (productDto.getImageFile().isEmpty()) {
			bindingResult.addError(new FieldError("productDto", "imageFile", "The image file is required"));
		}

		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.hasErrors());
			return "AddProduct";
		}

		MultipartFile image = productDto.getImageFile();
		Date createdAt = new Date();

		String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

		try {
			String uploadDir = "public/images/";
			java.nio.file.Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		Product product = new Product();
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getCategory());
		product.setCreated_At(createdAt);
		product.setDescription(productDto.getDescription());
		product.setImageFileName(storageFileName);
		product.setPrice(productDto.getPrice());

		productService.saveProduct(product);

		return "redirect:/product/indexPage";
	}

	@GetMapping("/edit")
	public String editProduct(@RequestParam int id, Model model) {
		try {
			Product product = productService.getProductById(id);
			model.addAttribute("product", product);

			ProductDto productDto = new ProductDto();
			productDto.setBrand(product.getBrand());
			productDto.setCategory(product.getCategory());
			productDto.setDescription(product.getDescription());
			productDto.setName(product.getName());
			productDto.setPrice(product.getPrice());

			model.addAttribute("productDto", productDto);

		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		}
		return "EditProduct";
	}

	@RequestMapping("/update")
	public String updateProduct(@RequestParam("id") int id, @Valid @ModelAttribute ProductDto productDto, Model model,
			BindingResult result) {

		try {
			Product product = productService.getProductById(id);

			model.addAttribute("product", product);

			if (result.hasErrors()) {
				return "EditProduct";
			}

			if (!productDto.getImageFile().isEmpty() || productDto.getImageFile().isEmpty()) {
				String uploadDir = "public/images/";
				java.nio.file.Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

				try {
					Files.delete(oldImagePath);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}

				MultipartFile image = productDto.getImageFile();
				Date createdAt = new Date();
				String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				System.out.println("storage" + storageFileName);
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}

				product.setImageFileName(storageFileName);
				product.setBrand(productDto.getBrand());
				product.setName(productDto.getName());
				product.setCategory(productDto.getCategory());
				product.setPrice(productDto.getPrice());
				product.setDescription(productDto.getDescription());

				productService.saveUpdatedProduct(product);

			}

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return "redirect:/product/indexPage";
	}

	@RequestMapping("/delete")
	public String deleteProduct(@RequestParam int id) {
		try {
			Product product = productService.getProductById(id);

			java.nio.file.Path imagePath = Paths.get("/public/images/" + product.getImageFileName());

			try {
				Files.delete(imagePath);
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
			}

			productService.deleteProduct(product);

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return "redirect:/product/indexPage";
	}

	@RequestMapping("/search1")
	public String searchProduct(@RequestParam("search") String search, Model model) {

		Product products = productService.getSearchProduct(search);

		if (products != null) {
			model.addAttribute("products", products);
		return "index";}
		
		else
			return "redirect:/product/indexPage";

	}

}
