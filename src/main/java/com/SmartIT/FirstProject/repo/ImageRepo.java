package com.SmartIT.FirstProject.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartIT.FirstProject.entity.Image;

public interface ImageRepo extends JpaRepository<Image, Long> {
	
    boolean existsByName(String name);


}
