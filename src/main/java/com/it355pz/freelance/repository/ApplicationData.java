package com.it355pz.freelance.repository;

import com.it355pz.freelance.model.Category;
import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ApplicationScope
public class ApplicationData {

    private final List<User> users = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final List<Skill> skills = new ArrayList<>();
    private final List<FreelanceJob> jobs = new ArrayList<>();
    private final List<Proposal> proposals = new ArrayList<>();
    private final List<CvAttachment> cvAttachments = new ArrayList<>();

    private final AtomicLong userIdSequence = new AtomicLong(1);
    private final AtomicLong categoryIdSequence = new AtomicLong(1);
    private final AtomicLong skillIdSequence = new AtomicLong(1);
    private final AtomicLong jobIdSequence = new AtomicLong(1);
    private final AtomicLong proposalIdSequence = new AtomicLong(1);
    private final AtomicLong cvAttachmentIdSequence = new AtomicLong(1);

    public Long nextUserId() {
        return userIdSequence.getAndIncrement();
    }

    public Long nextCategoryId() {
        return categoryIdSequence.getAndIncrement();
    }

    public Long nextSkillId() {
        return skillIdSequence.getAndIncrement();
    }

    public Long nextJobId() {
        return jobIdSequence.getAndIncrement();
    }

    public Long nextProposalId() {
        return proposalIdSequence.getAndIncrement();
    }

    public Long nextCvAttachmentId() {
        return cvAttachmentIdSequence.getAndIncrement();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<FreelanceJob> getJobs() {
        return jobs;
    }

    public List<Proposal> getProposals() {
        return proposals;
    }

    public List<CvAttachment> getCvAttachments() {
        return cvAttachments;
    }
}
