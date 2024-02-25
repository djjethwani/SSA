package com.studio.sa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studio.sa.bodies.AddCategory;
import com.studio.sa.entities.Categories;
import com.studio.sa.repo.CategoriesRepo;

@RestController
@CrossOrigin(origins = "*")
public class CategoryController {
	
	@Autowired
	private CategoriesRepo categoriesRepo;
	
	@GetMapping("/categories")
	public ResponseEntity<Object> getAllCategories(){
		return ResponseEntity.status(HttpStatus.OK).body(categoriesRepo.findAll());
	}
	
	@PostMapping("/addCategory")
	public ResponseEntity<Object> addCategory(@RequestBody AddCategory addCategory){
		
		Categories newCategory = new Categories();
		
		newCategory.setName(addCategory.getName());
		newCategory.setImageURL(addCategory.getImageurl());
		
		categoriesRepo.save(newCategory);
		
		return ResponseEntity.status(HttpStatus.OK).body("Successfull");
	}
}
