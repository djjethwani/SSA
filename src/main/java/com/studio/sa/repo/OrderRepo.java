package com.studio.sa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Orders;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {
	public List<Orders> findByUser(long user); 
}
