package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.Category;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.JobStatus;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.CategoryService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.SkillService;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FreelanceJobServiceImpl implements FreelanceJobService {

    private final ApplicationData data;
    private final CategoryService categoryService;
    private final SkillService skillService;
    private final UserService userService;

    public FreelanceJobServiceImpl(ApplicationData data, CategoryService categoryService,
                                   SkillService skillService, UserService userService) {
        this.data = data;
        this.categoryService = categoryService;
        this.skillService = skillService;
        this.userService = userService;
    }

    @Override
    public List<FreelanceJob> findAll() {
        return List.copyOf(data.getJobs());
    }

    @Override
    public List<FreelanceJob> findByClientId(Long clientId) {
        return data.getJobs().stream()
                .filter(job -> job.getClient().getId().equals(clientId))
                .toList();
    }

    @Override
    public Optional<FreelanceJob> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return data.getJobs().stream()
                .filter(job -> job.getId().equals(id))
                .findFirst();
    }

    @Override
    public FreelanceJob getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Posao nije pronadjen."));
    }

    @Override
    public List<FreelanceJob> search(String keyword, Long categoryId, BigDecimal minBudget, BigDecimal maxBudget) {
        return data.getJobs().stream()
                .filter(job -> matchesKeyword(job, keyword))
                .filter(job -> categoryId == null || job.getCategory().getId().equals(categoryId))
                .filter(job -> minBudget == null || job.getBudget().compareTo(minBudget) >= 0)
                .filter(job -> maxBudget == null || job.getBudget().compareTo(maxBudget) <= 0)
                .toList();
    }

    @Override
    public FreelanceJob create(String title, String description, BigDecimal budget, Long categoryId,
                               List<Long> skillIds, Long clientId) {
        validateJobData(title, description, budget);

        Category category = categoryService.getById(categoryId);
        List<Skill> skills = skillService.findAllByIds(skillIds);
        User client = userService.getById(clientId);

        if (!client.isClient()) {
            throw new ValidationException("Samo client korisnik moze da objavi posao.");
        }

        FreelanceJob job = new FreelanceJob(data.nextJobId(), title.trim(), description.trim(), budget,
                category, skills, client, LocalDateTime.now());

        synchronized (data.getJobs()) {
            data.getJobs().add(job);
        }

        return job;
    }

    @Override
    public FreelanceJob update(Long id, String title, String description, BigDecimal budget, Long categoryId,
                               List<Long> skillIds) {
        validateJobData(title, description, budget);

        FreelanceJob job = getById(id);
        Category category = categoryService.getById(categoryId);
        List<Skill> skills = skillService.findAllByIds(skillIds);

        synchronized (data.getJobs()) {
            job.setTitle(title.trim());
            job.setDescription(description.trim());
            job.setBudget(budget);
            job.setCategory(category);
            job.setRequiredSkills(skills);
        }

        return job;
    }

    @Override
    public boolean deleteById(Long id) {
        synchronized (data.getJobs()) {
            return data.getJobs().removeIf(job -> job.getId().equals(id));
        }
    }

    @Override
    public FreelanceJob grantJob(Long jobId, Long proposalId, Long clientId) {
        FreelanceJob job = getById(jobId);
        if (!job.getClient().getId().equals(clientId)) {
            throw new ValidationException("Mozes da odlucujes samo o svojim poslovima.");
        }

        Proposal proposal = data.getProposals().stream()
                .filter(item -> item.getId().equals(proposalId))
                .filter(item -> item.getJob().getId().equals(jobId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Prijava nije pronadjena."));

        synchronized (data.getJobs()) {
            job.setStatus(JobStatus.GRANTED);
            job.setGrantedFreelancer(proposal.getFreelancer());
            job.setGrantedAt(LocalDateTime.now());
        }

        return job;
    }

    @Override
    public long count() {
        return data.getJobs().size();
    }

    private boolean matchesKeyword(FreelanceJob job, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String normalizedKeyword = keyword.toLowerCase().trim();
        return job.getTitle().toLowerCase().contains(normalizedKeyword)
                || job.getDescription().toLowerCase().contains(normalizedKeyword);
    }

    private void validateJobData(String title, String description, BigDecimal budget) {
        if (isBlank(title)) {
            throw new ValidationException("Naziv posla je obavezan.");
        }

        if (isBlank(description)) {
            throw new ValidationException("Opis posla je obavezan.");
        }

        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Budzet mora biti veci od nule.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
