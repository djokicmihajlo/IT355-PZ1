package com.it355pz.freelance.service;

import com.it355pz.freelance.model.Proposal;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProposalService {

    List<Proposal> findAll();

    List<Proposal> findByJobId(Long jobId);

    Optional<Proposal> findById(Long id);

    Proposal getById(Long id);

    Proposal create(Long jobId, Long freelancerId, String proposalText, BigDecimal offeredPrice,
                    int estimatedDays, MultipartFile cvFile);

    long count();
}
