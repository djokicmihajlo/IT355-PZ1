package com.it355pz.freelance.controller;

import com.it355pz.freelance.controller.form.ProposalForm;
import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.service.FileStorageService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalDraftService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.ValidationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProposalController {

    private final ProposalService proposalService;
    private final FreelanceJobService freelanceJobService;
    private final FileStorageService fileStorageService;
    private final ProposalDraftService proposalDraftService;

    public ProposalController(ProposalService proposalService, FreelanceJobService freelanceJobService,
                              FileStorageService fileStorageService, ProposalDraftService proposalDraftService) {
        this.proposalService = proposalService;
        this.freelanceJobService = freelanceJobService;
        this.fileStorageService = fileStorageService;
        this.proposalDraftService = proposalDraftService;
    }

    @GetMapping({"/jobs/{jobId}/proposals", "/jobs/{jobId}/proposals/"})
    public String listJobProposals(@PathVariable Long jobId, Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        var job = freelanceJobService.getById(jobId);
        if (!currentUser.isClient() || !job.getClient().getId().equals(currentUser.getId())) {
            return "redirect:/jobs/" + jobId;
        }

        model.addAttribute("pageTitle", "Prijave za posao");
        model.addAttribute("job", job);
        model.addAttribute("proposals", proposalService.findByJobId(jobId));
        return "proposals/list";
    }

    @GetMapping({"/jobs/{jobId}/proposals/new", "/jobs/{jobId}/proposals/new/"})
    public String newProposalForm(@PathVariable Long jobId, Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isFreelancer()) {
            return "redirect:/jobs/" + jobId;
        }

        ProposalForm proposalForm = new ProposalForm();
        proposalForm.setFreelancerId(currentUser.getId());
        model.addAttribute("pageTitle", "Nova prijava");
        model.addAttribute("job", freelanceJobService.getById(jobId));
        model.addAttribute("proposalForm", proposalForm);
        return "proposals/form";
    }

    @PostMapping("/jobs/{jobId}/proposals/draft")
    public String generateDraft(@PathVariable Long jobId, @ModelAttribute ProposalForm proposalForm, Model model,
                                HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isFreelancer()) {
            return "redirect:/jobs/" + jobId;
        }

        try {
            proposalForm.setFreelancerId(currentUser.getId());
            String draft = proposalDraftService.generateDraft(jobId, currentUser.getId());
            proposalForm.setProposalText(draft);
            populateProposalFormModel(jobId, proposalForm, model);
            model.addAttribute("successMessage", "Predlog je generisan.");
        } catch (ValidationException | ResourceNotFoundException ex) {
            populateProposalFormModel(jobId, proposalForm, model);
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "proposals/form";
    }

    @PostMapping("/jobs/{jobId}/proposals")
    public String createProposal(@PathVariable Long jobId, @ModelAttribute ProposalForm proposalForm,
                                 @RequestParam("cvFile") MultipartFile cvFile, Model model, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isFreelancer()) {
            return "redirect:/jobs/" + jobId;
        }

        try {
            proposalForm.setFreelancerId(currentUser.getId());
            proposalService.create(jobId, currentUser.getId(), proposalForm.getProposalText(),
                    proposalForm.getOfferedPrice(), proposalForm.getEstimatedDays(), cvFile);
            return "redirect:/jobs/" + jobId;
        } catch (ValidationException | ResourceNotFoundException ex) {
            populateProposalFormModel(jobId, proposalForm, model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "proposals/form";
        }
    }

    @PostMapping("/jobs/{jobId}/proposals/{proposalId}/grant")
    public String grantProposal(@PathVariable Long jobId, @PathVariable Long proposalId, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isClient()) {
            return "redirect:/jobs/" + jobId;
        }

        freelanceJobService.grantJob(jobId, proposalId, currentUser.getId());
        return "redirect:/jobs/" + jobId + "/proposals";
    }

    @GetMapping("/proposals/{proposalId}/cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long proposalId, HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            throw new ResourceNotFoundException("CV nije dostupan.");
        }

        Proposal proposal = proposalService.getById(proposalId);
        boolean allowedClient = currentUser.isClient()
                && proposal.getJob().getClient().getId().equals(currentUser.getId());
        boolean allowedFreelancer = currentUser.isFreelancer()
                && proposal.getFreelancer().getId().equals(currentUser.getId());
        if (!allowedClient && !allowedFreelancer) {
            throw new ResourceNotFoundException("CV nije dostupan.");
        }

        CvAttachment attachment = proposal.getCvAttachment();
        Resource resource = fileStorageService.loadAsResource(attachment);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(attachment.getOriginalFileName())
                        .build()
                        .toString())
                .body(resource);
    }

    private void populateProposalFormModel(Long jobId, ProposalForm proposalForm, Model model) {
        model.addAttribute("pageTitle", "Nova prijava");
        model.addAttribute("job", freelanceJobService.getById(jobId));
        model.addAttribute("proposalForm", proposalForm);
    }

    private User currentUser(HttpSession session) {
        Object user = session.getAttribute(SessionKeys.CURRENT_USER);
        return user instanceof User currentUser ? currentUser : null;
    }
}
