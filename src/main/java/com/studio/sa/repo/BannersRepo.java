package com.studio.sa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Banners;

@Repository
public interface BannersRepo extends JpaRepository<Banners, Long> {
	List<Banners> findByActive(boolean active);
}
