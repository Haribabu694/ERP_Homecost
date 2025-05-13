package com.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.Login;

public interface LoginRepository extends JpaRepository<Login, Long> {
	
	Optional<Login> findByUsername(String username);

}
