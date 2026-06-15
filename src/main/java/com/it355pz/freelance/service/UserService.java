package com.it355pz.freelance.service;

import com.it355pz.freelance.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User getById(Long id);

    long count();
}
