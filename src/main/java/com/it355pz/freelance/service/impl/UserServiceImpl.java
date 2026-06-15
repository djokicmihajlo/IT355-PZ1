package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final ApplicationData data;

    public UserServiceImpl(ApplicationData data) {
        this.data = data;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.getUsers());
    }

    @Override
    public User getById(Long id) {
        return data.getUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen."));
    }

    @Override
    public long count() {
        return data.getUsers().size();
    }
}
