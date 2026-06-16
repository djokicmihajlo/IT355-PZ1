package com.it355pz.freelance.service;

import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProposalServiceTest {

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ApplicationData data;

    @Test
    void createStoresProposalAndCvMetadata() {
        long proposalCount = proposalService.count();
        long attachmentCount = data.getCvAttachments().size();

        Proposal proposal = proposalService.create(
                secondJobId(),
                firstFreelancerId(),
                "Imam iskustvo u organizaciji sadrzaja i mogu da isporucim resenje u dogovorenom roku.",
                new BigDecimal("390.00"),
                6,
                pdfCv()
        );

        assertEquals(proposalCount + 1, proposalService.count());
        assertEquals(attachmentCount + 1, data.getCvAttachments().size());
        assertNotNull(proposal.getCvAttachment().getId());
        assertTrue(proposal.getCvAttachment().getOriginalFileName().endsWith(".pdf"));
    }

    @Test
    void createRejectsUnsupportedCvFile() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "cvFile",
                "cv.txt",
                "text/plain",
                "plain text".getBytes()
        );

        assertThrows(ValidationException.class, () -> proposalService.create(
                thirdJobId(),
                firstFreelancerId(),
                "Validan tekst prijave.",
                new BigDecimal("100.00"),
                3,
                invalidFile
        ));
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
