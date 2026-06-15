package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.Category;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.CategoryService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ApplicationData data;

    public CategoryServiceImpl(ApplicationData data) {
        this.data = data;
    }

    @Override
    public List<Category> findAll() {
        return List.copyOf(data.getCategories());
    }

    @Override
    public Category getById(Long id) {
        return data.getCategories().stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Kategorija nije pronadjena."));
    }
}
