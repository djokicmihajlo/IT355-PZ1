package com.it355pz.freelance.controller;

import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationData data;

    @Test
    void listJobsPageRendersSeedJobs() throws Exception {
        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Freelance poslovi")))
                .andExpect(content().string(containsString("Izrada Spring MVC landing stranice")));
    }

    @Test
    void jobDetailsPageRendersSelectedJob() throws Exception {
        Long jobId = data.getJobs().get(0).getId();

        mockMvc.perform(get("/jobs/{id}", jobId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Opis posla")))
                .andExpect(content().string(containsString("Spring MVC")));
    }

    @Test
    void newJobPageRendersFormLookups() throws Exception {
        mockMvc.perform(get("/jobs/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Novi posao")))
                .andExpect(content().string(containsString("Spring Boot")));
    }

    @Test
    void postCreateJobRedirectsToDetailsPage() throws Exception {
        mockMvc.perform(post("/jobs")
                        .param("title", "MockMvc posao")
                        .param("description", "Posao kreiran kroz MVC test.")
                        .param("budget", "250.00")
                        .param("categoryId", data.getCategories().get(0).getId().toString())
                        .param("skillIds", data.getSkills().get(0).getId().toString())
                        .param("clientId", firstClientId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/jobs/*"));
    }

    private Long firstClientId() {
        return data.getUsers().stream()
                .filter(User::isClient)
                .findFirst()
                .map(User::getId)
                .orElseThrow();
    }
}
