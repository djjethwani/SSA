package com.studio.sa.controllers;

import org.hibernate.query.Page;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studio.sa.bodies.AddProduct;
import com.studio.sa.bodies.GetProducts;
import com.studio.sa.entities.Products;
import com.studio.sa.repo.ProductsJpaRepo;
import com.studio.sa.repo.ProductsRepo;

import jakarta.websocket.server.PathParam;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {
	
	@Autowired
	ProductsRepo productsRepo;
	
	@Autowired
	ProductsJpaRepo productsJpaRepo;
	
	@PostMapping("/addProduct")
	public ResponseEntity<Object> addProduct(@RequestBody AddProduct addproduct){
		
		Products newProduct = new Products();
		
		newProduct.setName(addproduct.getName());
		newProduct.setDescription(addproduct.getDescription());
		newProduct.setCategory(addproduct.getCategory());
		newProduct.setPrice(addproduct.getPrice());
		newProduct.setImageURL(addproduct.getImage());
		newProduct.setNumberOfPieces(addproduct.getNumberOfPieces());
		
		productsJpaRepo.save(newProduct);
		
		return ResponseEntity.status(HttpStatus.OK).body("Ok");
	}
	
	@GetMapping("/products")
	public ResponseEntity<Object> getproducts(@RequestParam( name = "limit") int limit, @RequestParam (required = false, name = "page") Integer page, @RequestParam (required = false, name = "category") String category){
		
		if(page == null) {
			page = 1;
		}
		Pageable pages = PageRequest.of(page-1, limit);
		
		
		
		if(category == null || category.length() == 0) {
			return ResponseEntity.status(HttpStatus.OK).body(productsRepo.findAll(pages));
		}else {
			JSONObject res = new JSONObject();
			res.put("content", productsRepo.findByCategory(category,pages));
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(res.toString());
		}
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getProduct(@PathVariable(name = "id") long id){
		return ResponseEntity.status(HttpStatus.OK).body(productsJpaRepo.findById(id));
	}
}
