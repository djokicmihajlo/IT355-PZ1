package com.it355pz.freelance.controller;

import com.it355pz.freelance.controller.form.JobForm;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.service.CategoryService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.SkillService;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final FreelanceJobService freelanceJobService;
    private final CategoryService categoryService;
    private final SkillService skillService;
    private final UserService userService;

    public JobController(FreelanceJobService freelanceJobService, CategoryService categoryService,
                         SkillService skillService, UserService userService) {
        this.freelanceJobService = freelanceJobService;
        this.categoryService = categoryService;
        this.skillService = skillService;
        this.userService = userService;
    }

    @GetMapping
    public String listJobs(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) BigDecimal minBudget,
                           @RequestParam(required = false) BigDecimal maxBudget,
                           Model model) {
        model.addAttribute("pageTitle", "Poslovi");
        model.addAttribute("jobs", freelanceJobService.search(keyword, categoryId, minBudget, maxBudget));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minBudget", minBudget);
        model.addAttribute("maxBudget", maxBudget);
        return "jobs/list";
    }

    @GetMapping("/{id}")
    public String jobDetails(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Detalji posla");
        model.addAttribute("job", freelanceJobService.getById(id));
        return "jobs/details";
    }

    @GetMapping("/new")
    public String newJobForm(Model model) {
        model.addAttribute("pageTitle", "Novi posao");
        model.addAttribute("jobForm", new JobForm());
        addFormLookups(model, true);
        return "jobs/form";
    }

    @PostMapping
    public String createJob(@ModelAttribute JobForm jobForm, Model model) {
        try {
            var job = freelanceJobService.create(
                    jobForm.getTitle(),
                    jobForm.getDescription(),
                    jobForm.getBudget(),
                    jobForm.getCategoryId(),
                    jobForm.getSkillIds(),
                    jobForm.getClientId()
            );
            return "redirect:/jobs/" + job.getId();
        } catch (ValidationException | ResourceNotFoundException ex) {
            model.addAttribute("pageTitle", "Novi posao");
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("jobForm", jobForm);
            addFormLookups(model, true);
            return "jobs/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Izmena posla");
        model.addAttribute("jobId", id);
        model.addAttribute("jobForm", JobForm.fromJob(freelanceJobService.getById(id)));
        addFormLookups(model, false);
        return "jobs/form";
    }

    @PostMapping("/{id}/edit")
    public String updateJob(@PathVariable Long id, @ModelAttribute JobForm jobForm, Model model) {
        try {
            freelanceJobService.update(
                    id,
                    jobForm.getTitle(),
                    jobForm.getDescription(),
                    jobForm.getBudget(),
                    jobForm.getCategoryId(),
                    jobForm.getSkillIds()
            );
            return "redirect:/jobs/" + id;
        } catch (ValidationException | ResourceNotFoundException ex) {
            model.addAttribute("pageTitle", "Izmena posla");
            model.addAttribute("jobId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("jobForm", jobForm);
            addFormLookups(model, false);
            return "jobs/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteJob(@PathVariable Long id) {
        freelanceJobService.deleteById(id);
        return "redirect:/jobs";
    }

    private void addFormLookups(Model model, boolean includeClients) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("skills", skillService.findAll());
        model.addAttribute("includeClients", includeClients);
        if (includeClients) {
            model.addAttribute("clients", findClients());
        }
    }

    private List<User> findClients() {
        return userService.findAll().stream()
                .filter(User::isClient)
                .toList();
    }
}
