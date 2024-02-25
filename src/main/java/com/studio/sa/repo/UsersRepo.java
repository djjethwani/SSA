package com.studio.sa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studio.sa.entities.Users;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {
	public List<Users> findByEmail(String email);
}
