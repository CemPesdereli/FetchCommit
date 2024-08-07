package com.cem.valven.service;

import com.cem.valven.entity.Developer;
import com.cem.valven.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public Developer saveDeveloper(Developer developer) {
        return developerRepository.save(developer);
    }

    public List<Developer> getAllDevelopers() {
        return developerRepository.findAll();
    }

    public Optional<Developer> getDeveloperByUsername(String username) {
        return developerRepository.findByUsername(username);
    }

}
