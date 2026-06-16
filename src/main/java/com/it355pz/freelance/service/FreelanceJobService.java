package com.it355pz.freelance.service;

import com.it355pz.freelance.model.FreelanceJob;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FreelanceJobService {

    List<FreelanceJob> findAll();

    List<FreelanceJob> findByClientId(Long clientId);

    Optional<FreelanceJob> findById(Long id);

    FreelanceJob getById(Long id);

    List<FreelanceJob> search(String keyword, Long categoryId, BigDecimal minBudget, BigDecimal maxBudget);

    FreelanceJob create(String title, String description, BigDecimal budget, Long categoryId,
                        List<Long> skillIds, Long clientId);

    FreelanceJob update(Long id, String title, String description, BigDecimal budget, Long categoryId,
                        List<Long> skillIds);

    boolean deleteById(Long id);

    FreelanceJob grantJob(Long jobId, Long proposalId, Long clientId);

    long count();
}
