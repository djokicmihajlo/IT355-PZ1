package com.it355pz.freelance.controller;

import com.it355pz.freelance.controller.form.JobForm;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.service.CategoryService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.SkillService;
import com.it355pz.freelance.service.ValidationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
@Controller
@RequestMapping("/jobs")
public class JobController {

    private final FreelanceJobService freelanceJobService;
    private final CategoryService categoryService;
    private final SkillService skillService;

    public JobController(FreelanceJobService freelanceJobService, CategoryService categoryService,
                         SkillService skillService) {
        this.freelanceJobService = freelanceJobService;
        this.categoryService = categoryService;
        this.skillService = skillService;
    }

    @GetMapping({"", "/"})
    public String listJobs(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) BigDecimal minBudget,
                           @RequestParam(required = false) BigDecimal maxBudget,
                           Model model, HttpSession session) {
        User currentUser = currentUser(session);
        model.addAttribute("pageTitle", "Poslovi");
        if (currentUser != null && currentUser.isClient()) {
            model.addAttribute("jobs", freelanceJobService.findByClientId(currentUser.getId()));
        } else {
            model.addAttribute("jobs", freelanceJobService.search(keyword, categoryId, minBudget, maxBudget));
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minBudget", minBudget);
        model.addAttribute("maxBudget", maxBudget);
        return "jobs/list";
    }

    @GetMapping({"/{id}", "/{id}/"})
    public String jobDetails(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Detalji posla");
        model.addAttribute("job", freelanceJobService.getById(id));
        return "jobs/details";
    }

    @GetMapping({"/new", "/new/"})
    public String newJobForm(Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient()) {
            return "redirect:/jobs";
        }

        model.addAttribute("pageTitle", "Novi posao");
        model.addAttribute("jobForm", new JobForm());
        addFormLookups(model);
        return "jobs/form";
    }

    @PostMapping
    public String createJob(@ModelAttribute JobForm jobForm, Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient()) {
            return "redirect:/jobs";
        }

        try {
            var job = freelanceJobService.create(
                    jobForm.getTitle(),
                    jobForm.getDescription(),
                    jobForm.getBudget(),
                    jobForm.getCategoryId(),
                    jobForm.getSkillIds(),
                    currentUser.getId()
            );
            return "redirect:/jobs/" + job.getId();
        } catch (ValidationException | ResourceNotFoundException ex) {
            model.addAttribute("pageTitle", "Novi posao");
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("jobForm", jobForm);
            addFormLookups(model);
            return "jobs/form";
        }
    }

    @GetMapping({"/{id}/edit", "/{id}/edit/"})
    public String editJobForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient() || !freelanceJobService.getById(id).getClient().getId().equals(currentUser.getId())) {
            return "redirect:/jobs/" + id;
        }

        model.addAttribute("pageTitle", "Izmena posla");
        model.addAttribute("jobId", id);
        model.addAttribute("jobForm", JobForm.fromJob(freelanceJobService.getById(id)));
        addFormLookups(model);
        return "jobs/form";
    }

    @PostMapping("/{id}/edit")
    public String updateJob(@PathVariable Long id, @ModelAttribute JobForm jobForm, Model model,
                            HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient() || !freelanceJobService.getById(id).getClient().getId().equals(currentUser.getId())) {
            return "redirect:/jobs/" + id;
        }

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
            addFormLookups(model);
            return "jobs/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteJob(@PathVariable Long id, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient() || !freelanceJobService.getById(id).getClient().getId().equals(currentUser.getId())) {
            return "redirect:/jobs/" + id;
        }

        freelanceJobService.deleteById(id);
        return "redirect:/jobs";
    }

    private void addFormLookups(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("skills", skillService.findAll());
    }

    private User currentUser(HttpSession session) {
        Object user = session.getAttribute(SessionKeys.CURRENT_USER);
        return user instanceof User currentUser ? currentUser : null;
    }
}
