package com.studio.sa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Cart;

@Repository
public interface CartJpaRepo extends JpaRepository<Cart, Long> {
	public List<Cart> findByUser(long user);
}
