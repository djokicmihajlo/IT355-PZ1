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
        assertEquals(2, data.getUsers().size());
        assertEquals(2, data.getCategories().size());
        assertEquals(4, data.getSkills().size());
        assertEquals(2, data.getJobs().size());
        assertEquals(1, data.getProposals().size());
        assertEquals(1, data.getCvAttachments().size());
    }

    @Test
    void seedDataContainsClientAndFreelancerUsers() {
        assertTrue(data.getUsers().stream().anyMatch(user -> UserRole.CLIENT.equals(user.getRole())));
        assertTrue(data.getUsers().stream().anyMatch(user -> UserRole.FREELANCER.equals(user.getRole())));
    }
}
