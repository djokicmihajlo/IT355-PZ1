package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.FileStorageService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ApplicationData data;
    private final FreelanceJobService freelanceJobService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public ProposalServiceImpl(ApplicationData data, FreelanceJobService freelanceJobService,
                               UserService userService, FileStorageService fileStorageService) {
        this.data = data;
        this.freelanceJobService = freelanceJobService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<Proposal> findAll() {
        return List.copyOf(data.getProposals());
    }

    @Override
    public List<Proposal> findByJobId(Long jobId) {
        return data.getProposals().stream()
                .filter(proposal -> proposal.getJob().getId().equals(jobId))
                .toList();
    }

    @Override
    public Optional<Proposal> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return data.getProposals().stream()
                .filter(proposal -> proposal.getId().equals(id))
                .findFirst();
    }

    @Override
    public Proposal getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prijava nije pronadjena."));
    }

    @Override
    public Proposal create(Long jobId, Long freelancerId, String proposalText, BigDecimal offeredPrice,
                           int estimatedDays, MultipartFile cvFile) {
        validateProposalData(proposalText, offeredPrice, estimatedDays);

        FreelanceJob job = freelanceJobService.getById(jobId);
        User freelancer = userService.getById(freelancerId);
        if (!freelancer.isFreelancer()) {
            throw new ValidationException("Samo freelancer korisnik moze da posalje prijavu.");
        }

        CvAttachment cvAttachment = fileStorageService.storeCv(cvFile);
        cvAttachment.setId(data.nextCvAttachmentId());

        Proposal proposal = new Proposal(data.nextProposalId(), job, freelancer, proposalText.trim(),
                offeredPrice, estimatedDays, cvAttachment, LocalDateTime.now());

        synchronized (data.getProposals()) {
            data.getCvAttachments().add(cvAttachment);
            data.getProposals().add(proposal);
        }

        return proposal;
    }

    @Override
    public long count() {
        return data.getProposals().size();
    }

    private void validateProposalData(String proposalText, BigDecimal offeredPrice, int estimatedDays) {
        if (proposalText == null || proposalText.isBlank()) {
            throw new ValidationException("Proposal tekst je obavezan.");
        }

        if (offeredPrice == null || offeredPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Ponudjena cena mora biti veca od nule.");
        }

        if (estimatedDays <= 0) {
            throw new ValidationException("Rok izrade mora biti veci od nule.");
        }
    }
}
