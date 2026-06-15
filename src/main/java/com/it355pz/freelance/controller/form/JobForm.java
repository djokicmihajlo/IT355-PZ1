package com.it355pz.freelance.controller.form;

import com.it355pz.freelance.model.FreelanceJob;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JobForm {

    private String title;
    private String description;
    private BigDecimal budget;
    private Long categoryId;
    private List<Long> skillIds = new ArrayList<>();
    private Long clientId;

    public static JobForm fromJob(FreelanceJob job) {
        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setDescription(job.getDescription());
        form.setBudget(job.getBudget());
        form.setCategoryId(job.getCategory().getId());
        form.setSkillIds(job.getRequiredSkills().stream()
                .map(skill -> skill.getId())
                .toList());
        form.setClientId(job.getClient().getId());
        return form;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
