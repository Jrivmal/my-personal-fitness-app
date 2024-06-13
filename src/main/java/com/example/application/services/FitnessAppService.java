package com.example.application.services;

import com.example.application.data.FitnessApp;
import com.example.application.data.FitnessAppRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FitnessAppService {

    private final FitnessAppRepository repository;

    public FitnessAppService(FitnessAppRepository repository) {
        this.repository = repository;
    }

    public Optional<FitnessApp> get(Long id) {
        return repository.findById(id);
    }

    public FitnessApp update(FitnessApp entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<FitnessApp> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<FitnessApp> list(Pageable pageable, Specification<FitnessApp> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
