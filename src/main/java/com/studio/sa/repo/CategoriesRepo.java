package com.studio.sa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Categories;

@Repository
public interface CategoriesRepo extends JpaRepository<Categories, Long>{

}
