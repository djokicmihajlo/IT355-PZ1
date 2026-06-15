package com.it355pz.freelance.service;

import com.it355pz.freelance.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category getById(Long id);
}
