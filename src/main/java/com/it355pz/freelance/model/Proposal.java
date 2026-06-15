package com.it355pz.freelance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Proposal {

    private Long id;
    private FreelanceJob job;
    private User freelancer;
    private String proposalText;
    private BigDecimal offeredPrice;
    private int estimatedDays;
    private CvAttachment cvAttachment;
    private LocalDateTime createdAt;

    public Proposal() {
    }

    public Proposal(Long id, FreelanceJob job, User freelancer, String proposalText, BigDecimal offeredPrice,
                    int estimatedDays, CvAttachment cvAttachment, LocalDateTime createdAt) {
        this.id = id;
        this.job = job;
        this.freelancer = freelancer;
        this.proposalText = proposalText;
        this.offeredPrice = offeredPrice;
        this.estimatedDays = estimatedDays;
        this.cvAttachment = cvAttachment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FreelanceJob getJob() {
        return job;
    }

    public void setJob(FreelanceJob job) {
        this.job = job;
    }

    public User getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(User freelancer) {
        this.freelancer = freelancer;
    }

    public String getProposalText() {
        return proposalText;
    }

    public void setProposalText(String proposalText) {
        this.proposalText = proposalText;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public int getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(int estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    public CvAttachment getCvAttachment() {
        return cvAttachment;
    }

    public void setCvAttachment(CvAttachment cvAttachment) {
        this.cvAttachment = cvAttachment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
