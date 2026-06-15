package com.it355pz.freelance.service;

import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;
import com.it355pz.freelance.repository.ApplicationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FreelanceJobServiceTest {

    @Autowired
    private FreelanceJobService freelanceJobService;

    @Autowired
    private ApplicationData data;

    @Test
    void createAddsNewJobToApplicationScopeRepository() {
        long countBefore = freelanceJobService.count();

        FreelanceJob created = freelanceJobService.create(
                "Implementacija CRUD modula",
                "Potrebno je napraviti CRUD tok za poslove u Spring MVC aplikaciji.",
                new BigDecimal("520.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        );

        assertEquals(countBefore + 1, freelanceJobService.count());
        assertTrue(freelanceJobService.findById(created.getId()).isPresent());

        freelanceJobService.deleteById(created.getId());
    }

    @Test
    void updateChangesExistingJobData() {
        FreelanceJob created = freelanceJobService.create(
                "Stari naziv posla",
                "Stari opis posla.",
                new BigDecimal("200.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        );

        FreelanceJob updated = freelanceJobService.update(
                created.getId(),
                "Novi naziv posla",
                "Novi opis posla.",
                new BigDecimal("650.00"),
                firstCategoryId(),
                List.of(firstSkillId())
        );

        assertEquals("Novi naziv posla", updated.getTitle());
        assertEquals(new BigDecimal("650.00"), updated.getBudget());

        freelanceJobService.deleteById(created.getId());
    }

    @Test
    void searchFiltersJobsByKeywordAndBudget() {
        FreelanceJob created = freelanceJobService.create(
                "Jedinstveni posao za pretragu",
                "Opis koji sadrzi poseban search termin.",
                new BigDecimal("900.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        );

        List<FreelanceJob> results = freelanceJobService.search(
                "poseban search",
                null,
                new BigDecimal("800.00"),
                new BigDecimal("950.00")
        );

        assertTrue(results.stream().anyMatch(job -> job.getId().equals(created.getId())));

        freelanceJobService.deleteById(created.getId());
    }

    @Test
    void deleteRemovesExistingJob() {
        FreelanceJob created = freelanceJobService.create(
                "Posao za brisanje",
                "Ovaj posao se koristi za proveru brisanja.",
                new BigDecimal("150.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        );

        assertTrue(freelanceJobService.deleteById(created.getId()));
        assertFalse(freelanceJobService.findById(created.getId()).isPresent());
    }

    @Test
    void createRejectsInvalidJobData() {
        assertThrows(ValidationException.class, () -> freelanceJobService.create(
                "",
                "Opis postoji.",
                new BigDecimal("100.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        ));

        assertThrows(ValidationException.class, () -> freelanceJobService.create(
                "Naziv postoji",
                "Opis postoji.",
                BigDecimal.ZERO,
                firstCategoryId(),
                List.of(firstSkillId()),
                firstClientId()
        ));
    }

    @Test
    void createRejectsFreelancerAsJobOwner() {
        assertThrows(ValidationException.class, () -> freelanceJobService.create(
                "Nevalidan vlasnik",
                "Freelancer ne moze da bude client za oglas.",
                new BigDecimal("100.00"),
                firstCategoryId(),
                List.of(firstSkillId()),
                firstFreelancerId()
        ));
    }

    private Long firstCategoryId() {
        return data.getCategories().get(0).getId();
    }

    private Long firstSkillId() {
        return data.getSkills().get(0).getId();
    }

    private Long firstClientId() {
        return data.getUsers().stream()
                .filter(User::isClient)
                .findFirst()
                .map(User::getId)
                .orElseThrow();
    }

    private Long firstFreelancerId() {
        return data.getUsers().stream()
                .filter(user -> UserRole.FREELANCER.equals(user.getRole()))
                .findFirst()
                .map(User::getId)
                .orElseThrow();
    }
}
