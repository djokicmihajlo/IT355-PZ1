package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim().toLowerCase();
        return data.getUsers().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(normalizedUsername))
                .findFirst();
    }

    @Override
    public User getById(Long id) {
        return data.getUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen."));
    }

    @Override
    public User register(String username, String password, String fullName, String email, UserRole role,
                         String profileSummary) {
        validateUserData(username, password, fullName, email, role);

        if (findByUsername(username).isPresent()) {
            throw new ValidationException("Korisnicko ime je vec zauzeto.");
        }

        User user = new User(data.nextUserId(), username.trim().toLowerCase(), password.trim(),
                fullName.trim(), email.trim(), role, trimToEmpty(profileSummary));

        synchronized (data.getUsers()) {
            data.getUsers().add(user);
        }

        return user;
    }

    @Override
    public User authenticate(String username, String password) {
        User user = findByUsername(username)
                .orElseThrow(() -> new ValidationException("Neispravno korisnicko ime ili lozinka."));

        if (password == null || !password.equals(user.getPassword())) {
            throw new ValidationException("Neispravno korisnicko ime ili lozinka.");
        }

        return user;
    }

    @Override
    public long count() {
        return data.getUsers().size();
    }

    private void validateUserData(String username, String password, String fullName, String email, UserRole role) {
        if (isBlank(username)) {
            throw new ValidationException("Korisnicko ime je obavezno.");
        }

        if (isBlank(password) || password.trim().length() < 4) {
            throw new ValidationException("Lozinka mora imati najmanje 4 karaktera.");
        }

        if (isBlank(fullName)) {
            throw new ValidationException("Ime i prezime su obavezni.");
        }

        if (isBlank(email) || !email.contains("@")) {
            throw new ValidationException("Email nije ispravan.");
        }

        if (role == null) {
            throw new ValidationException("Tip naloga je obavezan.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
