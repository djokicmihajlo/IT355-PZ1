package com.it355pz.freelance.repository;

import com.it355pz.freelance.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ApplicationDataTest {

    @Autowired
    private ApplicationData data;

    @Test
    void seedDataIsLoadedIntoApplicationScopeRepository() {
        assertTrue(data.getUsers().size() >= 2);
        assertTrue(data.getCategories().size() >= 2);
        assertTrue(data.getSkills().size() >= 4);
        assertTrue(data.getJobs().size() >= 2);
        assertTrue(data.getProposals().size() >= 1);
        assertTrue(data.getCvAttachments().size() >= 1);
    }

    @Test
    void seedDataContainsClientAndFreelancerUsers() {
        assertTrue(data.getUsers().stream().anyMatch(user -> UserRole.CLIENT.equals(user.getRole())));
        assertTrue(data.getUsers().stream().anyMatch(user -> UserRole.FREELANCER.equals(user.getRole())));
    }
}
