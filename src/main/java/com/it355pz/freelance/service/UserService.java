package com.it355pz.freelance.service;

import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    User getById(Long id);

    User register(String username, String password, String fullName, String email, UserRole role,
                  String profileSummary);

    User authenticate(String username, String password);

    long count();
}
