package com.cem.valven.repository;

import com.cem.valven.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<Developer,Long> {

    Optional<Developer> findByUsername (String username);
}
