package com.it355pz.freelance.controller;

import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.ProposalDraftService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationData data;

    @Autowired
    private ProposalService proposalService;

    @MockitoBean
    private ProposalDraftService proposalDraftService;

    @Test
    void proposalFormRendersForJob() throws Exception {
        mockMvc.perform(get("/jobs/{jobId}/proposals/new", firstJobId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nova proposal prijava")))
                .andExpect(content().string(containsString("CV fajl")));
    }

    @Test
    void proposalUploadRedirectsToProposalList() throws Exception {
        mockMvc.perform(multipart("/jobs/{jobId}/proposals", firstJobId())
                        .file(pdfCv())
                        .param("freelancerId", firstFreelancerId().toString())
                        .param("proposalText", "MVC upload test prijava.")
                        .param("offeredPrice", "380.00")
                        .param("estimatedDays", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs/" + firstJobId() + "/proposals"));
    }

    @Test
    void geminiDraftPopulatesProposalText() throws Exception {
        String draftText = "Postovani, mogu da realizujem trazeni posao kroz Spring MVC i Thymeleaf.";
        when(proposalDraftService.generateDraft(firstJobId(), firstFreelancerId())).thenReturn(draftText);

        mockMvc.perform(post("/jobs/{jobId}/proposals/draft", firstJobId())
                        .param("freelancerId", firstFreelancerId().toString())
                        .param("offeredPrice", "380.00")
                        .param("estimatedDays", "4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gemini draft je generisan.")))
                .andExpect(content().string(containsString(draftText)));
    }

    @Test
    void geminiDraftShowsValidationError() throws Exception {
        when(proposalDraftService.generateDraft(firstJobId(), null))
                .thenThrow(new ValidationException("Izaberi freelancera pre generisanja drafta."));

        mockMvc.perform(post("/jobs/{jobId}/proposals/draft", firstJobId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Izaberi freelancera pre generisanja drafta.")));
    }

    @Test
    void proposalListShowsCvDownloadLink() throws Exception {
        Proposal proposal = proposalService.create(
                firstJobId(),
                firstFreelancerId(),
                "Prijava za list test.",
                new BigDecimal("410.00"),
                5,
                pdfCv()
        );

        mockMvc.perform(get("/jobs/{jobId}/proposals", firstJobId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(proposal.getCvAttachment().getOriginalFileName())));
    }

    @Test
    void cvDownloadReturnsStoredFile() throws Exception {
        Proposal proposal = proposalService.create(
                firstJobId(),
                firstFreelancerId(),
                "Prijava za download test.",
                new BigDecimal("430.00"),
                7,
                pdfCv()
        );

        mockMvc.perform(get("/proposals/{proposalId}/cv", proposal.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test-cv.pdf")))
                .andExpect(content().contentType("application/pdf"));
    }

    private Long firstJobId() {
        return data.getJobs().get(0).getId();
    }

    private Long firstFreelancerId() {
        return data.getUsers().stream()
                .filter(User::isFreelancer)
                .findFirst()
                .map(User::getId)
                .orElseThrow();
    }

    private MockMultipartFile pdfCv() {
        return new MockMultipartFile(
                "cvFile",
                "test-cv.pdf",
                "application/pdf",
                "%PDF-1.4 test".getBytes()
        );
    }
}
