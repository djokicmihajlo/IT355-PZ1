package com.it355pz.freelance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it355pz.freelance.model.Category;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;
import com.it355pz.freelance.service.impl.GeminiProposalDraftService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeminiProposalDraftServiceTest {

    @Test
    void generateDraftFailsCleanlyWhenApiKeyIsMissing() {
        FreelanceJobService freelanceJobService = mock(FreelanceJobService.class);
        UserService userService = mock(UserService.class);

        when(freelanceJobService.getById(1L)).thenReturn(testJob());
        when(userService.getById(2L)).thenReturn(testFreelancer());

        ProposalDraftService proposalDraftService = new GeminiProposalDraftService(
                freelanceJobService,
                userService,
                RestClient.builder(),
                new ObjectMapper(),
                "",
                "gemini-3.5-flash",
                "https://generativelanguage.googleapis.com/v1beta",
                "missing-test.env"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> proposalDraftService.generateDraft(1L, 2L)
        );

        assertTrue(exception.getMessage().contains("Generisanje predloga nije podeseno"));
    }

    private FreelanceJob testJob() {
        return new FreelanceJob(
                1L,
                "Spring MVC aplikacija",
                "Potrebna je izrada Thymeleaf forme.",
                new BigDecimal("500.00"),
                new Category(1L, "Web razvoj", "Izrada web aplikacija"),
                List.of(new Skill(1L, "Spring MVC"), new Skill(2L, "Thymeleaf")),
                new User(1L, "client", "Client User", "client@example.com", UserRole.CLIENT, "Klijent"),
                LocalDateTime.now()
        );
    }

    private User testFreelancer() {
        return new User(
                2L,
                "freelancer",
                "Ana Markovic",
                "ana@example.com",
                UserRole.FREELANCER,
                "Spring MVC developer sa iskustvom u Thymeleaf aplikacijama."
        );
    }
}
