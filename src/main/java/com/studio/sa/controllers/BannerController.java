package com.studio.sa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studio.sa.bodies.AddBanner;
import com.studio.sa.entities.Banners;
import com.studio.sa.repo.BannersRepo;
import com.studio.sa.services.BannersService;

@CrossOrigin(origins = "*")
@RestController
public class BannerController {
	
	@Autowired
	BannersRepo bannersRepo;
	
	@Autowired
	BannersService bannerService;
	
	@GetMapping("/banners")
	public ResponseEntity<Object> getBanners() {
		return ResponseEntity.status(HttpStatus.OK).body(bannersRepo.findByActive(true));
	}
	
	@PostMapping("/addBanner")
	public ResponseEntity<Object> addBanner(@RequestBody AddBanner addBanner){
		
		Banners newBanner = new Banners();
		
		newBanner.setName(addBanner.getName());
		newBanner.setImageURL(addBanner.getUrl());
		newBanner.setDescription(addBanner.getDescription());
		newBanner.setActive(true);
		
		bannersRepo.save(newBanner);
		
		return ResponseEntity.status(HttpStatus.OK).body("Successfully added");
	}
}
