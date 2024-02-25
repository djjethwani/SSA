package com.studio.sa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Products;

@Repository
public interface ProductsRepo extends PagingAndSortingRepository<Products, Long> {
	List<Products> findByCategory(String category, Pageable pageable);
}
