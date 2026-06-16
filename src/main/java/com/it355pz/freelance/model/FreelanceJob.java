package com.it355pz.freelance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FreelanceJob {

    private Long id;
    private String title;
    private String description;
    private BigDecimal budget;
    private Category category;
    private List<Skill> requiredSkills = new ArrayList<>();
    private User client;
    private LocalDateTime createdAt;
    private JobStatus status = JobStatus.OPEN;
    private User grantedFreelancer;
    private LocalDateTime grantedAt;

    public FreelanceJob() {
    }

    public FreelanceJob(Long id, String title, String description, BigDecimal budget, Category category,
                        List<Skill> requiredSkills, User client, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.category = category;
        this.requiredSkills = requiredSkills;
        this.client = client;
        this.createdAt = createdAt;
        this.status = JobStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public User getGrantedFreelancer() {
        return grantedFreelancer;
    }

    public void setGrantedFreelancer(User grantedFreelancer) {
        this.grantedFreelancer = grantedFreelancer;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    public boolean isOpen() {
        return JobStatus.OPEN.equals(status);
    }

    public boolean isGranted() {
        return JobStatus.GRANTED.equals(status);
    }
}
