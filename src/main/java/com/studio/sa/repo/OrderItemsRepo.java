package com.studio.sa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.OrderItems;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, Long> {

}
