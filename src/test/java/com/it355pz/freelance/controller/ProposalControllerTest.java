package com.it355pz.freelance.controller;

import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.controller.SessionKeys;
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
        mockMvc.perform(get("/jobs/{jobId}/proposals/new", secondJobId())
                        .sessionAttr(SessionKeys.CURRENT_USER, firstFreelancer()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nova prijava")))
                .andExpect(content().string(containsString("CV fajl")));
    }

    @Test
    void proposalUploadRedirectsToProposalList() throws Exception {
        mockMvc.perform(multipart("/jobs/{jobId}/proposals", secondJobId())
                        .file(pdfCv())
                        .sessionAttr(SessionKeys.CURRENT_USER, firstFreelancer())
                        .param("proposalText", "MVC upload test prijava.")
                        .param("offeredPrice", "380.00")
                        .param("estimatedDays", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs/" + secondJobId()));
    }

    @Test
    void geminiDraftPopulatesProposalText() throws Exception {
        String draftText = "Postovani, mogu da realizujem trazeni posao u dogovorenom roku.";
        when(proposalDraftService.generateDraft(secondJobId(), firstFreelancerId())).thenReturn(draftText);

        mockMvc.perform(post("/jobs/{jobId}/proposals/draft", secondJobId())
                        .sessionAttr(SessionKeys.CURRENT_USER, firstFreelancer())
                        .param("offeredPrice", "380.00")
                        .param("estimatedDays", "4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Predlog je generisan.")))
                .andExpect(content().string(containsString(draftText)));
    }

    @Test
    void geminiDraftShowsValidationError() throws Exception {
        when(proposalDraftService.generateDraft(secondJobId(), firstFreelancerId()))
                .thenThrow(new ValidationException("Predlog trenutno nije dostupan."));

        mockMvc.perform(post("/jobs/{jobId}/proposals/draft", secondJobId())
                        .sessionAttr(SessionKeys.CURRENT_USER, firstFreelancer()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Predlog trenutno nije dostupan.")));
    }

    @Test
    void proposalListShowsCvDownloadLink() throws Exception {
        Proposal proposal = proposalService.create(
                thirdJobId(),
                secondFreelancerId(),
                "Prijava za list test.",
                new BigDecimal("410.00"),
                5,
                pdfCv()
        );

        mockMvc.perform(get("/jobs/{jobId}/proposals", thirdJobId())
                        .sessionAttr(SessionKeys.CURRENT_USER, ownerOfThirdJob()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(proposal.getCvAttachment().getOriginalFileName())));
    }

    @Test
    void cvDownloadReturnsStoredFile() throws Exception {
        Proposal proposal = proposalService.create(
                thirdJobId(),
                firstFreelancerId(),
                "Prijava za download test.",
                new BigDecimal("430.00"),
                7,
                pdfCv()
        );

        mockMvc.perform(get("/proposals/{proposalId}/cv", proposal.getId())
                        .sessionAttr(SessionKeys.CURRENT_USER, proposal.getFreelancer()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test-cv.pdf")))
                .andExpect(content().contentType("application/pdf"));
    }

    private Long firstJobId() {
        return data.getJobs().get(0).getId();
    }

    private Long secondJobId() {
        return data.getJobs().get(1).getId();
    }

    private Long thirdJobId() {
        return data.getJobs().get(2).getId();
    }

    private Long firstFreelancerId() {
        return firstFreelancer().getId();
    }

    private Long secondFreelancerId() {
        return data.getUsers().stream()
                .filter(User::isFreelancer)
                .skip(1)
                .findFirst()
                .map(User::getId)
                .orElseThrow();
    }

    private User firstFreelancer() {
        return data.getUsers().stream()
                .filter(User::isFreelancer)
                .findFirst()
                .orElseThrow();
    }

    private User ownerOfThirdJob() {
        return data.getJobs().get(2).getClient();
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
