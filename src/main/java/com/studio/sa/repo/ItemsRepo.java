package com.studio.sa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Items;

@Repository
public interface ItemsRepo extends JpaRepository<Items, Long> {

}
