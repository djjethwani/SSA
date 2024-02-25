package com.studio.sa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Products;

@Repository
public interface ProductsJpaRepo extends JpaRepository<Products, Long> {
	
}
